package com.libra.frame.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import com.libra.base.BaseApp
import com.libra.frame.BuildConfig
import com.libra.frame.api.DataManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.io.File
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.properties.Delegates


/**
 * Created by libra on 2017/10/30.
 */
class App : BaseApp() {

    companion object {
        var instance: App by Delegates.notNull()
    }

    private val PREFS_FILE = "device_id.xml"
    private val PREFS_DEVICE_ID = "device_id"
    var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        DataManager.instance.init(this)
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {

            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
                currentActivity = activity
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }

        })
    }

    /**
     * 为每个设备产生唯一的UUID，以ANDROID_ID为基础，在获
     * 取失败时以TelephonyManager.getDeviceId()为备选方法，如果再失败，使用UUID的生成策略
     */
    @SuppressLint("DefaultLocale", "MissingPermission")
    fun getDeviceUuid(): String? {
        synchronized(this) {
            val prefs = getSharedPreferences(PREFS_FILE, 0)
            var id = prefs.getString(PREFS_DEVICE_ID, null)
            if (id != null) {
                return id
            } else {
                var uuid: UUID
                val androidId = Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                try {
                    if ("9774d56d682e549c" != androidId) { // Android
                        // 2.2中的缺陷，受影响的设备具有相同的ANDROID_ID,就是9774d56d682e549c
                        uuid = UUID.nameUUIDFromBytes(androidId.toByteArray(charset("utf8")))
                    } else {
                        val deviceId = (getSystemService(
                            Context.TELEPHONY_SERVICE
                        ) as TelephonyManager).deviceId
                        uuid = if (deviceId != null) UUID.nameUUIDFromBytes(
                            deviceId.toByteArray(charset("utf8"))
                        )
                        else UUID.randomUUID()
                    }
                } catch (e: UnsupportedEncodingException) {
                    uuid = UUID.randomUUID()
                }

                id = uuid.toString().replace("-", "").toUpperCase()
                prefs.edit().putString(PREFS_DEVICE_ID, id).apply()
                return id
            }
        }
    }

    override fun onCrash(thread: Thread?, ex: Throwable?) {
    }

    override fun onCrashReport(file: File?) {
    }

    override fun onCrashRestarted() {
    }
}