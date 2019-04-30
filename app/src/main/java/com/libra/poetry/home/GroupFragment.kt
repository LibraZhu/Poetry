package com.libra.poetry.home


import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.libra.base.BaseRecyclerViewFragment
import com.libra.base.BaseXmlModel
import com.libra.frame.api.Api
import com.libra.frame.api.data.Banner
import com.libra.frame.api.data.Comment
import com.libra.frame.utils.GlideRoundTransform
import com.libra.poetry.R
import com.libra.poetry.home.xmlmodel.GroupItemXmlModel
import com.libra.superrecyclerview.SuperRecyclerView
import com.libra.utils.dp2px
import com.libra.utils.toast
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_group.*

class GroupFragment : BaseRecyclerViewFragment<com.libra.poetry.databinding.FragmentGroupBinding>() {

    companion object {
        fun newInstance(): GroupFragment {
            val fragment = GroupFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var headerView: LinearLayout? = null
    override fun getLayoutID(): Int {
        return R.layout.fragment_group
    }

    override fun initRecycleView() {
        super.initRecycleView()
        val scrollView = HorizontalScrollView(context)
        scrollView.setPadding(0, context?.dp2px(10f) ?: 0, 0, context?.dp2px(10f) ?: 0)
        headerView = LinearLayout(context)
        scrollView.addView(headerView)
        refreshBannerView(ArrayList())
        addHeaderView(scrollView)

        recyclerView.setRefreshing(true)
        onRefresh()
    }

    override fun onRefresh() {
        addObservable(
            Api.getInstance().banner()
                .success(Consumer {
                    refreshBannerView(it)
                })
                .error(Consumer {
                })
        )

        addObservable(
            Api.getInstance().commentList(1)
                .success(Consumer {
                    if (it.page == 1) {
                        getBaseAdapter().clear()
                    }
                    getBaseAdapter().append(it.rows ?: ArrayList())
                    if ((it.totalRows ?: 0) <= (getBaseAdapter().getData()?.size ?: 0).toLong()) {
                        getRecyclerView().isLoadingMore = true
                    }
                    getRecyclerView().setRefreshing(false)
                })
                .error(Consumer {
                    context?.toast(it.message)
                    getRecyclerView().setRefreshing(false)
                    getRecyclerView().isLoadingMore = false
                    getRecyclerView().moreProgressView.visibility = View.GONE
                })
        )
    }

    private fun refreshBannerView(list: ArrayList<Banner>) {
        if (headerView != null) {
            for (banner in list) {
                val imageView = ImageView(context)
                val w = (resources.displayMetrics.widthPixels - 4 * (context?.dp2px(16f) ?: 0)) / 3
                val h = w * 192 / 108
                Glide.with(imageView.context).load(banner.image)
                    .apply(RequestOptions().transform(GlideRoundTransform(6)).override(w, h)).into(imageView)
                headerView?.addView(imageView, w, h)
                (imageView.layoutParams as LinearLayout.LayoutParams).leftMargin = context?.dp2px(16f) ?: 0
            }
        }
    }

    override fun getRecyclerView(): SuperRecyclerView {
        return recyclerView
    }

    override fun isCanRefresh(): Boolean {
        return true
    }

    override fun isCanLoadMore(): Boolean {
        return true
    }

    override fun getItemLayoutID(): Int {
        return R.layout.item_group
    }

    override fun initItemXmlModel(item: Any?, binding: ViewDataBinding): BaseXmlModel {
        val itemXmlModel = GroupItemXmlModel()
        if (item is Comment?) {
            itemXmlModel.userName.set(getString(R.string.format_user_poetry, item?.fromUserName, item?.poetryTitle))
            itemXmlModel.userAvator.set(item?.fromUserAvatar)
            itemXmlModel.content.set(item?.content)
            itemXmlModel.time.set(item?.createTime)
            itemXmlModel.like.set((item?.likeNum ?: 0).toString())
            itemXmlModel.reply.set((item?.replyNum ?: 0).toString())
            itemXmlModel.userClick = View.OnClickListener {}
            itemXmlModel.replyClick = View.OnClickListener {}
            itemXmlModel.likeClick = View.OnClickListener {}
            itemXmlModel.poetryClick = View.OnClickListener {}
            itemXmlModel.followClick = View.OnClickListener {}
        }
        return itemXmlModel
    }

    override fun initLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

    override fun initXmlModel() {
    }

    override fun updateView() {
    }

}
