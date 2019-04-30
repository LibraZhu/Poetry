package com.libra.poetry.setting.xmlmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import com.libra.base.BaseXmlModel
import java.util.*

/**
 * @author Libra
 * @since 2018/8/30
 */
class SettingViewModel : BaseXmlModel() {
    var questionClick: View.OnClickListener? = null
    var suggestClick: View.OnClickListener? = null
    var modifyPassClick: View.OnClickListener? = null
    var aboutPassClick: View.OnClickListener? = null
    var logoutClick: View.OnClickListener? = null
    var loginFlag = ObservableBoolean(false)
}