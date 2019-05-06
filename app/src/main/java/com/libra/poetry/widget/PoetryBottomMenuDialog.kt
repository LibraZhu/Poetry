package com.libra.poetry.widget

import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import com.libra.frame.widget.BottomSheetDialog
import com.libra.poetry.R

/**
 * @author Libra
 * @since 2019/4/30
 */
class PoetryBottomMenuDialog : BottomSheetDialogFragment() {

    private var rootView: View? = null
    protected var ddialog: Dialog? = null
    private var mContext: Context? = null
    protected var mBehavior: BottomSheetBehavior<*>? = null
    protected var textSizeChangeListener: TextSizeChangeListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onStart() {
        super.onStart()
        val window = ddialog?.window
        val windowParams = window?.attributes
        //这里设置透明度
        windowParams?.dimAmount = 0.5f

        window?.attributes = windowParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        ddialog = BottomSheetDialog(context!!, theme)
        if (rootView == null) {
            //缓存下来的View 当为空时才需要初始化 并缓存
            initRootView()
        }
        ddialog?.setContentView(rootView)
        mBehavior = BottomSheetBehavior.from(rootView?.parent as View)
        (rootView?.parent as View).setBackgroundColor(Color.TRANSPARENT)
        rootView?.post {
            /**
             * PeekHeight默认高度256dp 会在该高度上悬浮
             * 设置等于view的高 就不会卡住
             */
            /**
             * PeekHeight默认高度256dp 会在该高度上悬浮
             * 设置等于view的高 就不会卡住
             */
            mBehavior?.peekHeight = rootView?.height ?: 0
        }

        return ddialog!!
    }

    private fun initRootView() {
        val databinding = DataBindingUtil.inflate<com.libra.poetry.databinding.DialogPoetryMenuBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_poetry_menu,
            null,
            false
        )
        databinding.bgLayout.setOnClickListener { }
        databinding.fontLayout.setOnClickListener { }
        databinding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        databinding.small.setOnCheckedChangeListener { _, b ->
            if (b) {
                textSizeChangeListener?.onTextSizeChange(14)
            }
        }
        databinding.mid.setOnCheckedChangeListener { _, b ->
            if (b) {
                textSizeChangeListener?.onTextSizeChange(16)
            }
        }
        databinding.large.setOnCheckedChangeListener { _, b ->
            if (b) {
                textSizeChangeListener?.onTextSizeChange(18)
            }
        }
        rootView = databinding.root
    }

    interface TextSizeChangeListener {
        fun onTextSizeChange(size: Int)
    }
}