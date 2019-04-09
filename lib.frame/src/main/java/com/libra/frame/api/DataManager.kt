package com.libra.frame.api

import android.annotation.SuppressLint
import android.app.Application
import android.preference.PreferenceManager
import android.text.TextUtils
import com.libra.frame.api.data.Config
import com.libra.utils.JSONUtils
import com.libra.frame.api.data.User

/**
 * Created by libra on 2017/10/30.
 */

class DataManager private constructor() {

    private var context: Application? = null
    private var curUser: User? = null
    var config: Config? = null
    /**
     * 获取用户
     */
    private fun getUser(): User? = JSONUtils.fromJson(
            PreferenceManager.getDefaultSharedPreferences(context).getString("user", "{}"),
            User::class.java)


    /**
     * 保存用户
     */
    private fun saveUser(user: User) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("user",
                JSONUtils.toJson(
                        user)).apply()
    }

    /**
     * 是否已登录
     *
     * @return true:已登录 ; false:没有
     */
    fun isLogin(): Boolean = !TextUtils.isEmpty(getToken())

    /**
     * 获取登录信息
     *
     * @return 账号、密码（md5）
     */
    fun getLoginInfo(): Array<String> = arrayOf(
            PreferenceManager.getDefaultSharedPreferences(context).getString("loginAccount", ""),
            PreferenceManager.getDefaultSharedPreferences(context).getString("loginPassword", ""))

    /**
     * 初始化
     *
     * @param context context
     */
    fun init(context: Application) {
        this.context = context
    }

    /**
     * 返回当前登录用户
     *
     * @return user
     */
    fun getCurUser(): User? {
        if (curUser == null) {
            curUser = getUser()
        }
        return curUser
    }

    /**
     * 设置当前用户
     *
     * @param user 用户
     */
    fun setCurUser(user: User) {
        curUser = user
        saveUser(user)
    }
    /**
     * 保存当前用户token信息
     *
     * @param accessToken accessToken
     */

    /**
     * 获取登录过用户的Token
     *
     * @return accessToken
     */
    fun getToken(): String? = PreferenceManager.getDefaultSharedPreferences(context).getString(
            "accessToken", null)

    /**
     * 保存token
     */
    fun saveToken(accessToken: String?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("accessToken",
                accessToken).apply()
    }

    /**
     * 清除登录用户的token信息
     */
    fun clearToken() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove("accessToken").apply()
    }

    /**
     * 保存登录信息
     *
     * @param loginAccount 账号
     * @param loginPassword 密码
     */
    fun saveLoginInfo(loginAccount: String, loginPassword: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("loginAccount",
                loginAccount).apply()
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("loginPassword",
                loginPassword).apply()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        val instance = DataManager()
    }
}
