package com.libra.poetry.home.xmlmodel

import android.databinding.ObservableField
import android.view.View
import com.libra.base.BaseXmlModel

/**
 * @author Libra
 * @since 2019/4/10
 */
class PoetryItemXmlModel : BaseXmlModel() {
    var title = ObservableField("")
    var author = ObservableField("")
    var content = ObservableField("")
    var itemClick: View.OnClickListener? = null
}