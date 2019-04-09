package com.libra.login

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.libra.base.BaseBindingActivity
import com.libra.frame.api.Api
import com.libra.frame.api.DataManager
import com.libra.login.xmlmodel.LoginXmlModel
import com.libra.utils.hideSoftKeyboard
import com.libra.utils.startActivity
import com.libra.utils.startActivityForResult
import com.libra.utils.toast
import io.reactivex.functions.Consumer

class LoginActivity : BaseBindingActivity<com.libra.login.databinding.ActivityLoginBinding>() {

    companion object {
        fun start(activity: Activity) {
            activity.startActivityForResult<LoginActivity>(0x0001)
        }
    }

    private val xmlModel: LoginXmlModel by lazy { LoginXmlModel() }
    override fun getLayoutID(): Int = R.layout.activity_login

    override fun initIntentData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val params = window.attributes
            params.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or params.flags
        }
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
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
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
            setResult(Activity.RESULT_OK)
            finish()
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
