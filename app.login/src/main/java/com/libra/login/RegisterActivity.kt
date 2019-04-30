package com.libra.login

import android.app.Activity
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import com.libra.base.BaseBindingActivity
import com.libra.frame.api.Api
import com.libra.frame.api.DataManager
import com.libra.frame.utils.StatusBarLight
import com.libra.login.xmlmodel.LoginXmlModel
import com.libra.utils.hideSoftKeyboard
import com.libra.utils.isMobileNo
import com.libra.utils.startActivityForResult
import com.libra.utils.toast
import io.reactivex.functions.Consumer
import java.util.*

class RegisterActivity : BaseBindingActivity<com.libra.login.databinding.ActivityRegisterBinding>() {
    companion object {
        fun start(activity: Activity) {
            activity.startActivityForResult<RegisterActivity>(0x0001)
        }
    }

    private val xmlModel: LoginXmlModel by lazy { LoginXmlModel() }
    override fun getLayoutID(): Int {
        return R.layout.activity_register
    }

    override fun initToolBar() {
        StatusBarLight.light(window)
        super.initToolBar()
    }

    override fun initIntentData() {
    }

    override fun initXmlModel() {
        xmlModel.codeText.set(getString(R.string.get_code))
        xmlModel.registerClick = View.OnClickListener {
            doRegister(
                xmlModel.account.get() ?: "", xmlModel.password.get() ?: "",
                xmlModel.confirmPassword.get() ?: "", xmlModel.code.get() ?: ""
            )
        }
        xmlModel.getCodeClick = View.OnClickListener { doSendCode(xmlModel.account.get() ?: "") }
        xmlModel.agreeClick = View.OnClickListener { doAgree() }
        binding?.vm = xmlModel
    }

    private fun doAgree() {
        WebActivity.start(this, "用户协议", DataManager.instance.config?.registerUrl)
    }

    private fun doRegister(phone: String, password: String, confirmPassword: String, code: String) {
        if (TextUtils.isEmpty(phone)) {
            toast(getString(R.string.toast_please_input_phone))
            return
        }
        if (!phone.isMobileNo()) {
            toast(getString(R.string.toast_please_input_phone_error))
            return
        }
        if (TextUtils.isEmpty(code)) {
            toast(getString(R.string.toast_please_input_validateCode))
            return
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            toast(getString(R.string.toast_please_input_password))
            return
        }
        if (password.length < 6 || password.length > 16) {
            toast(getString(R.string.input_password))
            return
        }
        if (!password.contentEquals(confirmPassword)) {
            toast(getString(R.string.toast_please_input_password_not_same))
            return
        }
        if (!xmlModel.agree.get()) {
            toast(getString(R.string.toast_agree))
            return
        }
        currentFocus?.hideSoftKeyboard()
        showLoadingDialog()
        addObservable(
            Api.getInstance().register(phone, password, confirmPassword, code)
                .success(Consumer { it ->
                    addObservable(Api.getInstance().login(phone, password)
                        .success(Consumer {
                            closeLoadingDialog()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }).error(Consumer {
                            closeLoadingDialog()
                            toast(getString(R.string.toast_auto_login_fail))
                            finish()
                        })
                    )
                })
                .error(Consumer { t ->
                    closeLoadingDialog()
                    toast(t?.message)
                })
        )
    }

    private fun doSendCode(phone: String) {
        if (TextUtils.isEmpty(phone)) {
            toast(getString(R.string.toast_please_input_phone))
            return
        }
        if (!phone.isMobileNo()) {
            toast(getString(R.string.toast_please_input_phone_error))
            return
        }
        showLoadingDialog()
        addObservable(Api.getInstance().getRegisterCode(phone)
            .success(Consumer {
                closeLoadingDialog()
                CodeTimer((1 * 60 * 1000).toLong(), 1000).start()
            }).error(Consumer { t ->
                closeLoadingDialog()
                toast(t?.message)
            })
        )
    }

    internal inner class CodeTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(
        millisInFuture, countDownInterval
    ) {


        override fun onFinish() {
            xmlModel.isSendBtnEnable.set(true)
            xmlModel.codeText.set(getString(R.string.get_code))
        }


        override fun onTick(millisUntilFinished: Long) {
            xmlModel.isSendBtnEnable.set(false)
            xmlModel.codeText.set(
                String.format(
                    Locale.getDefault(),
                    getString(R.string.getCodeFormat),
                    millisUntilFinished.toInt() / 1000
                )
            )
        }
    }
}
