package com.libra.login

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.libra.base.BaseBindingActivity
import com.libra.frame.api.Api
import com.libra.frame.api.DataManager
import com.libra.frame.utils.StatusBarLight
import com.libra.login.xmlmodel.LoginXmlModel
import com.libra.utils.hideSoftKeyboard
import com.libra.utils.startActivity
import com.libra.utils.toast
import io.reactivex.functions.Consumer

class LoginActivity : BaseBindingActivity<com.libra.login.databinding.ActivityLoginBinding>() {
    companion object {
        var callback: ((Activity) -> Unit?)? = null
        fun start(activity: Activity, callback: (act: Activity) -> Unit) {
            this.callback = callback
            activity.startActivity<LoginActivity>()
        }
    }

    private val xmlModel: LoginXmlModel by lazy { LoginXmlModel() }
    override fun getLayoutID(): Int = R.layout.activity_login

    override fun initIntentData() {
    }

    override fun initToolBar() {
        StatusBarLight.lightFullScreen(window)
        super.initToolBar()
    }

    override fun initXmlModel() {
        xmlModel.codeText.set(getString(R.string.get_code))
        xmlModel.loginClick = View.OnClickListener {
            doLogin(xmlModel.account.get() ?: "", xmlModel.password.get() ?: "")
        }
        xmlModel.forgotClick = View.OnClickListener { doForgotPassword() }
        xmlModel.registerClick = View.OnClickListener { doRegister() }
        binding?.vm = xmlModel
    }

    override fun onStart() {
        super.onStart()
        val loginInfo = DataManager.instance.getLoginInfo()
        xmlModel.account.set(loginInfo[0])
        xmlModel.password.set(loginInfo[1])
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x0001) {
                if (callback != null) {
                    callback?.invoke(this)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }

    private fun doLogin(phone: String, password: String) {
        if (TextUtils.isEmpty(phone)) {
            toast(getString(R.string.toast_please_input_phone))
            return
        }
        if (TextUtils.isEmpty(password)) {
            toast(getString(R.string.toast_please_input_password))
            return
        }
        currentFocus.hideSoftKeyboard()
        showLoadingDialog()
        addObservable(Api.getInstance().login(phone, password).success(Consumer {
            closeLoadingDialog()
            if (callback != null) {
                callback?.invoke(this)
            }
        }).error(Consumer { t ->
            closeLoadingDialog()
            toast(t?.message)
        }))
    }

    private fun doRegister() {
        RegisterActivity.start(this)
    }

    private fun doForgotPassword() {
        ForgotPasswordActivity.start(this)
    }
}
