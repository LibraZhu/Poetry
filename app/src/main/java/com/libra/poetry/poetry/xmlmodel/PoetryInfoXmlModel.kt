package com.libra.poetry.poetry.xmlmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.databinding.ObservableLong
import android.view.View
import com.libra.base.BaseXmlModel

/**
 * @author Libra
 * @since 2019/4/24
 */
class PoetryInfoXmlModel : BaseXmlModel() {
    var fav = ObservableBoolean(false)
    var title = ObservableField("")
    var subTitle = ObservableField("")
    var content = ObservableField("")
    var commentSize = ObservableLong(0)
    var anim = ObservableBoolean(false)

    var favClick: View.OnClickListener? = null
    var commentClick: View.OnClickListener? = null
    var shareClick: View.OnClickListener? = null
    var contentClick: View.OnClickListener? = null
    var remarkClick: View.OnClickListener? = null
    var translationClick: View.OnClickListener? = null
    var appreciationClick: View.OnClickListener? = null
    var authorClick: View.OnClickListener? = null
    var animClick: View.OnClickListener? = null
    var nullClick: View.OnClickListener? = null

}