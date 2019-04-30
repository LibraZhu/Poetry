package com.libra.poetry.home


import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import com.libra.base.BaseRecyclerViewFragment
import com.libra.base.BaseXmlModel
import com.libra.frame.api.Api
import com.libra.frame.api.data.Poetry
import com.libra.poetry.R
import com.libra.poetry.home.xmlmodel.PoetryItemXmlModel
import com.libra.poetry.poetry.PoetryInfoActivity
import com.libra.superrecyclerview.SuperRecyclerView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_poetry.*


class PoetryFragment : BaseRecyclerViewFragment<com.libra.poetry.databinding.FragmentPoetryBinding>() {

    companion object {
        fun newInstance(): PoetryFragment {
            val fragment = PoetryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_poetry
    }

    override fun initRecycleView() {
        super.initRecycleView()
        val headerPoetryBinding = DataBindingUtil.inflate<com.libra.poetry.databinding.HeaderPoetryBinding>(
            LayoutInflater.from(context),
            R.layout.header_poetry,
            null,
            false
        )
        headerPoetryBinding.banner.layoutParams.height = resources.displayMetrics.widthPixels * 683 / 1024
        addHeaderView(headerPoetryBinding.root)

        recyclerView.setRefreshing(true)
        onRefresh()
    }

    override fun onRefresh() {
        addObservable(
            Api.getInstance().recommend()
                .success(Consumer {
                    getRecyclerView().setRefreshing(false)
                    getBaseAdapter().setData(it)
                })
                .error(Consumer {
                    getRecyclerView().setRefreshing(false)
                })
        )
    }

    override fun isCanRefresh(): Boolean {
        return true
    }

    override fun getRecyclerView(): SuperRecyclerView {
        return recyclerView
    }

    override fun getItemLayoutID(): Int {
        return R.layout.item_poetry
    }

    override fun initItemXmlModel(item: Any?, binding: ViewDataBinding): BaseXmlModel {
        val itemXmlModel = PoetryItemXmlModel()
        if (item is Poetry?) {
            itemXmlModel.title.set(item?.title)
            itemXmlModel.author.set(item?.dynasty + " " + item?.author)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                (binding as com.libra.poetry.databinding.ItemPoetryBinding).content.text =
                    Html.fromHtml(item?.content, Html.FROM_HTML_MODE_COMPACT)
            } else {
                (binding as com.libra.poetry.databinding.ItemPoetryBinding).content.text = Html.fromHtml(item?.content)
            }
            itemXmlModel.itemClick = View.OnClickListener { PoetryInfoActivity.start(activity!!, item) }
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
