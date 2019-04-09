package com.libra.frame.utils

import android.databinding.BindingAdapter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * Created by libra on 2017/8/4.
 */
@BindingAdapter("isBold")
fun textBold(textView: TextView, isBold: Boolean) {
    if (isBold) {
        textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    } else {
        textView.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    }
}

@BindingAdapter("uri", "placeholder")
fun imageUri(imageView: ImageView, uri: String?, placeholder: Drawable?) {
    if (imageView.layoutParams.width > 0) {
        Glide.with(imageView.context).load(uri).apply(
                RequestOptions().override(imageView.layoutParams.width,
                                          imageView.layoutParams.height).placeholder(
                        placeholder)).into(imageView)
    } else {
        Glide.with(imageView.context).load(uri).apply(
                RequestOptions().placeholder(placeholder)).into(imageView)
    }
}