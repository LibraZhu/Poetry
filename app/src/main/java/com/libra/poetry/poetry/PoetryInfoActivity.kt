package com.libra.poetry.poetry

import android.app.Activity
import android.os.Handler
import android.text.Html
import android.view.*
import android.view.animation.AnimationUtils
import com.libra.base.BaseBindingActivity
import com.libra.frame.api.Api
import com.libra.frame.api.data.Poetry
import com.libra.frame.utils.StatusBarLight
import com.libra.poetry.R
import com.libra.poetry.poetry.xmlmodel.PoetryInfoXmlModel
import com.libra.utils.startActivity
import com.libra.utils.toast
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_poetry_info.*

class PoetryInfoActivity : BaseBindingActivity<com.libra.poetry.databinding.ActivityPoetryInfoBinding>() {

    companion object {
        fun start(activity: Activity, poetry: Poetry?) {
            activity.startActivity<PoetryInfoActivity>("poetry" to poetry)
        }
    }

    private var poetry: Poetry? = null
    private val xmlModel: PoetryInfoXmlModel by lazy { PoetryInfoXmlModel() }
    override fun getLayoutID(): Int {
        return R.layout.activity_poetry_info
    }

    override fun initToolBar() {
        StatusBarLight.light(window)
        super.initToolBar()
    }

    override fun initIntentData() {
        poetry = intent.getSerializableExtra("poetry") as Poetry?
    }

    override fun initXmlModel() {
        xmlModel.title.set(poetry?.title)
        xmlModel.subTitle.set("[" + poetry?.dynasty + "]  " + poetry?.author)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            content.text = Html.fromHtml(poetry?.content, Html.FROM_HTML_MODE_COMPACT)
        } else {
            content.text = Html.fromHtml(poetry?.content)
        }
        xmlModel.favClick = View.OnClickListener { doFav() }
        xmlModel.commentClick = View.OnClickListener { doComment() }
        xmlModel.shareClick = View.OnClickListener { doShare() }
        xmlModel.contentClick = View.OnClickListener { doContent() }
        xmlModel.remarkClick = View.OnClickListener { doRemark() }
        xmlModel.translationClick = View.OnClickListener { doTranslation() }
        xmlModel.appreciationClick = View.OnClickListener { doAppreciation() }
        xmlModel.authorClick = View.OnClickListener { doAuthor() }
        xmlModel.animClick = View.OnClickListener {
            if (xmlModel.anim.get()) {
                val anim1 = AnimationUtils.loadAnimation(this, R.anim.slide_in_down)
                val anim2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
                anim1.fillAfter = true
                anim2.fillAfter = true
                bottomLayout.startAnimation(anim1)
                rightLayout.startAnimation(anim2)
            } else {
                val anim1 = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)
                val anim2 = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
                anim1.fillAfter = true
                anim2.fillAfter = true
                bottomLayout.startAnimation(anim1)
                rightLayout.startAnimation(anim2)
            }
            xmlModel.anim.set(!xmlModel.anim.get())
        }
        xmlModel.nullClick = View.OnClickListener { }
        binding?.xmlmodel = xmlModel
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                xmlModel.animClick?.onClick(scrollView)
                return true
            }
        })
        scrollView.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener gestureDetector.onTouchEvent(
                motionEvent
            )
        }
        Handler().postDelayed({ xmlModel.animClick?.onClick(scrollView) }, 1000)
        checkFav()
        commentList()
    }

    private fun commentList() {
        addObservable(
            Api.getInstance().commentList(1, poetry?.id ?: 0)
                .success(Consumer {
                    xmlModel.commentSize.set(it.totalRows?:0)
                })
                .error(Consumer {

                })
        )
    }

    private fun checkFav() {
        addObservable(
            Api.getInstance().favCheck(poetry?.id ?: 0)
                .success(Consumer {
                    xmlModel.fav.set(it)
                })
                .error(Consumer {

                })
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(Menu.NONE, Menu.FIRST + 1, 0, getString(R.string.menu_more))?.setIcon(R.drawable.ic_menu_more)
            ?.setShowAsActionFlags(
                MenuItem.SHOW_AS_ACTION_IF_ROOM
            )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == Menu.FIRST + 1) {

        }
        return super.onOptionsItemSelected(item)
    }

    private fun convertContent(content: String): String {
        val list = content.replace("<P>", "").replace("</p>", "")
            .replace("<br>", "").replace("</br>", "")
        return ""
    }

    private fun doFav() {
        showLoadingDialog()
        if (xmlModel.fav.get()) {
            addObservable(
                Api.getInstance().favDelete(poetry?.id ?: 0)
                    .success(Consumer {
                        closeLoadingDialog()
                        xmlModel.fav.set(false)
                    })
                    .error(Consumer {
                        closeLoadingDialog()
                        toast(it.message)
                    })
            )
        } else {
            addObservable(
                Api.getInstance().favAdd(poetry?.id ?: 0)
                    .success(Consumer {
                        closeLoadingDialog()
                        xmlModel.fav.set(true)
                    })
                    .error(Consumer {
                        closeLoadingDialog()
                        toast(it.message)
                    })
            )
        }
    }

    private fun doComment() {
        PoetryCommentActivity.start(this, poetry)
    }

    private fun doShare() {

    }

    private fun doContent() {

    }

    private fun doRemark() {

    }

    private fun doTranslation() {

    }

    private fun doAppreciation() {

    }

    private fun doAuthor() {

    }
}
