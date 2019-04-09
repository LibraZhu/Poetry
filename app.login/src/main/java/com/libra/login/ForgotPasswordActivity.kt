package com.libra.login

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import com.libra.base.BaseBindingActivity
import com.libra.frame.api.Api
import com.libra.frame.utils.StatusBarLight
import com.libra.login.xmlmodel.LoginXmlModel
import com.libra.utils.hideSoftKeyboard
import com.libra.utils.isMobileNo
import com.libra.utils.startActivityForResult
import com.libra.utils.toast
import io.reactivex.functions.Consumer
import java.util.*

class ForgotPasswordActivity : BaseBindingActivity<com.libra.login.databinding.ActivityForgotPasswordBinding>() {
    companion object {
        fun start(activity: Activity) {
            activity.startActivityForResult<ForgotPasswordActivity>(0x0001)
        }
    }

    private val xmlModel: LoginXmlModel by lazy { LoginXmlModel() }
    override fun getLayoutID(): Int {
        return R.layout.activity_forgot_password
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarLight.light(window)
        super.onCreate(savedInstanceState)
    }

    override fun initIntentData() {
    }

    override fun initXmlModel() {
        xmlModel.codeText.set(getString(R.string.get_code))
        xmlModel.forgotClick = View.OnClickListener {
            doModify(
                xmlModel.account.get()
                    ?: "", xmlModel.password.get() ?: "", xmlModel.confirmPassword.get() ?: "",
                xmlModel.code.get() ?: ""
            )
        }
        xmlModel.getCodeClick = View.OnClickListener { doSendCode(xmlModel.account.get() ?: "") }
        binding?.vm = xmlModel
    }

    private fun doModify(phone: String, password: String, confirmPassword: String, code: String) {
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
        if (password != confirmPassword) {
            toast(getString(R.string.toast_please_input_password_not_same))
            return
        }
        currentFocus?.hideSoftKeyboard()
        showLoadingDialog()
        addObservable(
            Api.getInstance().forgotPass(phone, password, confirmPassword, code)
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
                }).error(Consumer { t ->
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
        addObservable(Api.getInstance().getForgotPasswordCode(phone)
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
