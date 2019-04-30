package com.libra.poetry.home.xmlmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import com.libra.base.BaseXmlModel

/**
 * @author Libra
 * @since 2019/4/10
 */
class GroupItemXmlModel : BaseXmlModel() {
    var userName = ObservableField("")
    var userAvator = ObservableField("")
    var content = ObservableField("")
    var time = ObservableField("")
    var like = ObservableField("")
    var reply = ObservableField("")
    var follow = ObservableBoolean(false)

    var likeFlag = ObservableBoolean(false)
    var userClick: View.OnClickListener? = null
    var likeClick: View.OnClickListener? = null
    var replyClick: View.OnClickListener? = null
    var poetryClick: View.OnClickListener? = null
    var followClick: View.OnClickListener? = null
}