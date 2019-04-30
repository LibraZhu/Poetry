package com.libra.poetry.home.xmlmodel

import android.databinding.ObservableField
import android.view.View
import com.libra.base.BaseXmlModel

/**
 * @author Libra
 * @since 2019/4/10
 */
class UserXmlModel : BaseXmlModel() {
    var name = ObservableField("")
    var follow = ObservableField("")
    var fans = ObservableField("")
    var nameClick: View.OnClickListener? = null
    var followClick: View.OnClickListener? = null
    var fansClick: View.OnClickListener? = null
    var favClick: View.OnClickListener? = null
    var friendClick: View.OnClickListener? = null
    var messageClick: View.OnClickListener? = null
}