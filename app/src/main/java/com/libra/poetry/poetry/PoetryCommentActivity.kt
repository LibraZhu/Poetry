package com.libra.poetry.poetry

import android.app.Activity
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.libra.base.BaseRecyclerViewActivity
import com.libra.base.BaseXmlModel
import com.libra.frame.api.Api
import com.libra.frame.api.data.Comment
import com.libra.frame.api.data.Poetry
import com.libra.poetry.R
import com.libra.poetry.poetry.xmlmodel.PoetryCommentXmlModel
import com.libra.superrecyclerview.SuperRecyclerView
import com.libra.utils.startActivity
import com.libra.utils.toast
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_poetry_comment.*

class PoetryCommentActivity : BaseRecyclerViewActivity<com.libra.poetry.databinding.ActivityPoetryCommentBinding>() {

    companion object {
        fun start(activity: Activity, poetry: Poetry?) {
            activity.startActivity<PoetryCommentActivity>("poetry" to poetry)
        }
    }

    private var page = 1
    private var poetry: Poetry? = null
    override fun getLayoutID(): Int {
        return R.layout.activity_poetry_comment
    }

    override fun initIntentData() {
        poetry = intent.getSerializableExtra("poetry") as Poetry?
    }

    override fun initToolBar() {
        super.initToolBar()
        title = poetry?.title
    }

    override fun initXmlModel() {

    }

    override fun initCustomView() {
        send.setOnClickListener {
            if (!TextUtils.isEmpty(inputComment.text.toString())) {
                showLoadingDialog()
                addObservable(
                    Api.getInstance()
                        .commentAdd(
                            poetry?.id ?: 0, poetry?.title ?: "", inputComment.text.toString()
                        )
                        .success(Consumer {
                            closeLoadingDialog()
                            onRefresh()
                        })
                        .error(Consumer {
                            closeLoadingDialog()
                            toast(it.message)
                        })
                )
            }
        }
        recyclerView.setRefreshing(true)
        onRefresh()
    }

    private fun getComment() {
        addObservable(
            Api.getInstance().commentList(page, poetry?.id)
                .success(Consumer {
                    if (it.page == 1) {
                        getBaseAdapter().clear()
                    }
                    getBaseAdapter().append(it.rows ?: ArrayList())
                    if ((it.totalRows ?: 0) <= (getBaseAdapter().getData()?.size ?: 0).toLong()) {
                        getRecyclerView().isLoadingMore = true
                    }
                    getRecyclerView().setRefreshing(false)
                    page++
                })
                .error(Consumer {
                    toast(it.message)
                    getRecyclerView().setRefreshing(false)
                    getRecyclerView().isLoadingMore = false
                    getRecyclerView().moreProgressView.visibility = View.GONE
                })
        )
    }

    override fun getRecyclerView(): SuperRecyclerView {
        return recyclerView
    }

    override fun onRefresh() {
        page = 1
        getComment()
    }

    override fun onLoadMore(overallItemsCount: Int, itemsBeforeMore: Int, maxLastVisiblePositio: Int) {
        getComment()
    }

    override fun isCanRefresh(): Boolean {
        return true
    }

    override fun isCanLoadMore(): Boolean {
        return true
    }

    override fun getItemLayoutID(): Int {
        return R.layout.item_poetry_comment
    }


    override fun initItemXmlModel(item: Any?, binding: ViewDataBinding): BaseXmlModel {
        val itemXmlModel = PoetryCommentXmlModel()
        if (item is Comment?) {
            itemXmlModel.userName.set(item?.fromUserName)
            itemXmlModel.userAvator.set(item?.fromUserAvatar)
            itemXmlModel.content.set(item?.content)
            itemXmlModel.time.set(item?.createTime)
            itemXmlModel.like.set((item?.likeNum ?: 0).toString())
            itemXmlModel.reply.set((item?.replyNum ?: 0).toString())
            itemXmlModel.userClick = View.OnClickListener {}
            itemXmlModel.replyClick = View.OnClickListener {}
            itemXmlModel.likeClick = View.OnClickListener {}
        }
        return itemXmlModel
    }

    override fun initLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this)
    }
}
