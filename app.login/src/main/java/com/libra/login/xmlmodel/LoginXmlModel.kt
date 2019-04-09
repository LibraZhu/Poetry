package com.libra.login.xmlmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import com.libra.base.BaseXmlModel

/**
 * @author Libra
 * @since 2018/8/23
 */
class LoginXmlModel : BaseXmlModel() {
    var agree = ObservableBoolean(true)
    var smsLogin = ObservableBoolean(false)
    var account = ObservableField<String>("")
    var password = ObservableField<String>("")
    var code = ObservableField<String>("")
    var codeText = ObservableField<String>("")
    var isSendBtnEnable = ObservableBoolean(true)
    var getCodeClick: View.OnClickListener? = null
    var loginClick: View.OnClickListener? = null

    var confirmPassword = ObservableField<String>("")
    var registerClick: View.OnClickListener? = null
    var agreeClick: View.OnClickListener? = null
    var forgotClick: View.OnClickListener? = null
}