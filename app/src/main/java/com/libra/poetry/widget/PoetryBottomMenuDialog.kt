package com.libra.poetry.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import android.widget.LinearLayout
import com.libra.frame.widget.BottomSheetDialog

/**
 * @author Libra
 * @since 2019/4/30
 */
class PoetryBottomMenuDialog : BottomSheetDialogFragment() {

    private var rootView: LinearLayout? = null
    protected var ddialog: Dialog? = null
    private var mContext: Context? = null
    protected var mBehavior: BottomSheetBehavior<*>? = null

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

    }
}