package com.libra.poetry.home


import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import com.libra.base.BaseRecyclerViewFragment
import com.libra.base.BaseXmlModel
import com.libra.frame.Constants
import com.libra.frame.api.Api
import com.libra.login.LoginActivity
import com.libra.poetry.R
import com.libra.poetry.home.xmlmodel.UserItemXmlModel
import com.libra.poetry.home.xmlmodel.UserXmlModel
import com.libra.poetry.setting.SettingActivity
import com.libra.superrecyclerview.SuperRecyclerView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : BaseRecyclerViewFragment<com.libra.poetry.databinding.FragmentUserBinding>() {

    companion object {
        fun newInstance(): UserFragment {
            val fragment = UserFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private val xmlModel: UserXmlModel by lazy { UserXmlModel() }
    override fun getLayoutID(): Int {
        return R.layout.fragment_user
    }

    override fun initRecycleView() {
        super.initRecycleView()
        val headerUserBinding = DataBindingUtil.inflate<com.libra.poetry.databinding.HeaderUserBinding>(
            LayoutInflater.from(context),
            R.layout.header_user,
            null,
            false
        )
        setting.setOnClickListener { SettingActivity.start(activity!!) }
        xmlModel.nameClick = View.OnClickListener { }
        xmlModel.followClick = View.OnClickListener { }
        xmlModel.fansClick = View.OnClickListener { }
        xmlModel.favClick = View.OnClickListener { }
        xmlModel.friendClick = View.OnClickListener { }
        xmlModel.messageClick = View.OnClickListener { }
        headerUserBinding.xmlmodel = xmlModel
        addHeaderView(headerUserBinding.root)

        updateView()
    }

    override fun getRecyclerView(): SuperRecyclerView {
        return recyclerView
    }

    override fun getItemLayoutID(): Int {
        return R.layout.item_user
    }

    override fun initItemXmlModel(item: Any?, binding: ViewDataBinding): BaseXmlModel {
        return UserItemXmlModel()
    }

    override fun initLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

    override fun initXmlModel() {
    }

    override fun updateView() {
        addObservable(
            Api.getInstance()
                .userInfo()
                .success(Consumer {
                    xmlModel.name.set(it.nickname)
                    xmlModel.fans.set(getString(R.string.fans, it.fans))
                    xmlModel.follow.set(getString(R.string.follow, it.follow))
                })
                .error(Consumer {
                    if (it.code == Constants.ErrorCode.Error_Token) {
                        LoginActivity.start(activity!!) { act ->
                            MainActivity.start(act)
                            act.finish()
                        }
                        activity?.finish()
                    }
                })
        )
    }


}
