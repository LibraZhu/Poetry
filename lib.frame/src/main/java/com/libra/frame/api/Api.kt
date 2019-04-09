package com.libra.frame.api

import android.os.Build
import android.support.v4.util.ArrayMap
import android.util.Base64
import com.libra.api.ApiException
import com.libra.api.ApiObservable
import com.libra.api.RetrofitBuilder
import com.libra.frame.Constants
import com.libra.frame.R
import com.libra.frame.api.data.Config
import com.libra.frame.api.data.Response
import com.libra.frame.api.data.User
import com.libra.frame.app.App
import com.libra.utils.JSONUtils
import com.libra.utils.MD5
import com.libra.utils.getVersionCode
import io.reactivex.Flowable
import okhttp3.MediaType
import okhttp3.RequestBody
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import kotlin.properties.Delegates


/**
 * Created by libra on 2017/10/30.
 */
class Api private constructor() {
    private var url = "https://api.dikuaifu.com"
    private var mApiService: ApiService by Delegates.notNull()

    private val ANDROID_PLATFORM = "Android"
    private val AMAZON_PLATFORM = "amazon-fireos"
    private val AMAZON_DEVICE = "Amazon"

    init {
        url = App.instance.getString(R.string.sAddress)
        val retrofit = RetrofitBuilder.build(url, 6)
        mApiService = retrofit.create(ApiService::class.java)
    }


    private object SingletonHolder {
        internal val INSTANCE = Api()
    }

    companion object {
        fun getInstance(): Api {
            return SingletonHolder.INSTANCE
        }
    }

    private fun buildDeviceInfo(): String {
        val map = ArrayMap<String, String>()
        map["did"] = App.instance.getDeviceUuid()
        map["pub"] = "Android"
        map["dtp"] = getPlatform()
        map["dver"] = getOSVersion()
        map["ver"] = "" + App.instance.getVersionCode()
        map["dnm"] = String.format("%s %s", getManufacturer(), getModel())
        return JSONUtils.toJson(map)
    }


    /**
     * Get the OS name.
     */
    fun getPlatform(): String {
        val platform: String
        if (isAmazonDevice()) {
            platform = AMAZON_PLATFORM
        } else {
            platform = ANDROID_PLATFORM
        }
        return platform
    }


    fun getModel(): String {
        return Build.MODEL
    }


    fun getProductName(): String {
        return Build.PRODUCT
    }


    fun getManufacturer(): String {
        return Build.MANUFACTURER
    }


    fun getSerialNumber(): String {
        return Build.SERIAL
    }


    /**
     * Get the OS version.
     */
    fun getOSVersion(): String {
        return Build.VERSION.RELEASE
    }


    fun getSDKVersion(): String {
        return Build.VERSION.SDK
    }


    fun getTimeZoneID(): String {
        val tz = TimeZone.getDefault()
        return tz.id
    }

    fun isAmazonDevice(): Boolean {
        return android.os.Build.MANUFACTURER == AMAZON_DEVICE
    }

    /**
     * 接口参数 form
     */
    private fun buildJsonParams(dataParams: TreeMap<String, Any?>): RequestBody {
        val map = TreeMap<String, Any>()
        map["timestamp"] = (System.currentTimeMillis() / 1000).toInt()
        if (dataParams.size == 0) {
            map["param"] = ""
        } else {
            map["param"] = dataParams
        }
        map["clientType"] = "ANDROID"
        map["token"] = DataManager.instance.getToken() ?: ""
        map["deviceId"] = App.instance.getDeviceUuid() ?: ""
        map["ipaddr"] = getIPAddress(true)
        map["sign"] = buildSignature(map)
        return RequestBody.create(MediaType.parse("Content-Type, application/json"), JSONUtils.toJson(map))
    }

    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<InetAddress>()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) continue
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement())
                }
            }
            for (add in adds) {
                if (!add.isLoopbackAddress()) {
                    val hostAddress = add.getHostAddress()
                    val isIPv4 = hostAddress.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return hostAddress
                    } else {
                        if (!isIPv4) {
                            val index = hostAddress.indexOf('%')
                            return if (index < 0)
                                hostAddress.toUpperCase()
                            else
                                hostAddress.substring(0, index).toUpperCase()
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 签名
     */
    private fun buildSignature(dataParams: TreeMap<String, Any>): String {
        val sign = StringBuilder()
        for (entry in dataParams) {
            val o = entry.value
            if (o == null) {
                sign.append(entry.key).append("=").append("&")
            } else {
                when (o) {
                    is String -> sign.append(entry.key).append("=").append(o).append("&")
                    is Boolean -> sign.append(entry.key).append("=").append(if (o) 1 else 0).append(
                        "&"
                    )
                    is Map<*, *> -> {
                        sign.append(entry.key).append("=")
                        for (entryParam in o) {
                            sign.append(entryParam.key).append("=").append(entryParam.value).append("&")
                        }
                    }
                    else -> sign.append(entry.key).append("=").append(o).append("&")
                }
            }
        }
        return Base64.encodeToString(sign.substring(0, sign.length - 1).toByteArray(), Base64.NO_WRAP)
    }

    /**
     * 过滤成功 错误码
     *
     * @param flowable 原始接口flowable
     * @param <T> 解析对象
     * @return 转化后的flowable
     */
    private fun <T : Response<*>> checkSuccess(flowable: Flowable<T>): Flowable<T> {
        return flowable.map { t ->
            if (t.isSuccess) {
                t
            } else {
                if (t.code == Constants.ErrorCode.Error_Token) {//token失效或未登录
                    DataManager.instance.clearToken()
                }
                throw ApiException.error(t.code, t.msg)
            }
        }
    }

    fun getConfig(): ApiObservable<Config> {
        val map = TreeMap<String, Any?>()
        return ApiObservable<Config>().observable(
            checkSuccess(mApiService.getConfig(buildJsonParams(map))).map { t ->
                if (t.data == null) {
                    t.data = Config()
                }
                DataManager.instance.config = t.data
                return@map t.data
            })
    }

    /**
     * 注册
     */
    fun register(username: String, password: String, password_confirm: String, code: String): ApiObservable<Any> {
        val map = TreeMap<String, Any?>()
        map["phone"] = username
        map["password"] = MD5.encode(MD5.encode(password))
        map["password_confirmation"] = MD5.encode(MD5.encode(password_confirm))
        map["verification_code"] = code
        return ApiObservable<Any>().observable(
            checkSuccess(mApiService.register(buildJsonParams(map))).map {
                DataManager.instance.saveLoginInfo(username, "")
                return@map true
            })
    }

    /**
     * 登出状态忘记密码
     */
    fun forgotPass(mobile: String, password: String, password_confirmation: String, code: String): ApiObservable<Any> {
        val map = TreeMap<String, Any?>()
        map["phone"] = mobile
        map["password"] = MD5.encode(MD5.encode(password))
        map["password_confirmation"] = MD5.encode(MD5.encode(password_confirmation))
        map["verification_code"] = code
        return ApiObservable<Any>().observable(
            checkSuccess(mApiService.forgotPassword(buildJsonParams(map))).map { return@map true })
    }

    /**
     * 登入状态修改密码
     */
    fun modifyPass(old_password: String, password: String, password_confirmation: String): ApiObservable<Any> {
        val map = TreeMap<String, Any?>()
        map["old_password"] = MD5.encode(MD5.encode(old_password))
        map["password"] = MD5.encode(MD5.encode(password))
        map["password_confirmation"] = MD5.encode(MD5.encode(password_confirmation))
        return ApiObservable<Any>().observable(
            checkSuccess(mApiService.modifyPassword(buildJsonParams(map))).map { return@map true })
    }

    /**
     * 获取短信验证码
     */
    fun getRegisterCode(mobile: String): ApiObservable<Any> {
        return getCode(mobile, Constants.ValidateCode.Register)
    }

    /**
     * 获取短信验证码
     */
    fun getForgotPasswordCode(mobile: String): ApiObservable<Any> {
        return getCode(mobile, Constants.ValidateCode.ForgotPassword)
    }

    /**
     * 获取短信验证码, type:1：注册，2：找回密码
     */
    private fun getCode(mobile: String, type: Int): ApiObservable<Any> {
        val map = TreeMap<String, Any?>()
        map["phone"] = mobile
        map["send_type"] = type
        return ApiObservable<Any>().observable(
            checkSuccess(mApiService.getcode(buildJsonParams(map))).map { return@map true })
    }


    /**
     * 登录
     */
    fun login(username: String, password: String): ApiObservable<User> {
        val map = TreeMap<String, Any?>()
        map["account"] = username
        map["password"] = password
        return ApiObservable<User>().observable(
            checkSuccess(mApiService.login(buildJsonParams(map))).map { t ->
                if (t.data == null) {
                    t.data = User()
                }
                DataManager.instance.setCurUser(t.data!!)
                DataManager.instance.saveLoginInfo(username, password)
                DataManager.instance.saveToken(t.data?.token)
                return@map t.data
            })
    }

    /**
     * 用户信息
     */
    fun userInfo(): ApiObservable<User> {
        val map = TreeMap<String, Any?>()
        return ApiObservable<User>().observable(
            checkSuccess(mApiService.userInfo(buildJsonParams(map))).map { t ->
                if (t.data == null) {
                    t.data = User()
                }
                DataManager.instance.setCurUser(t.data!!)
                return@map t.data
            })
    }
}