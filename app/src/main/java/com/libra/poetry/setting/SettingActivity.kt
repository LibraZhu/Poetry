package com.libra.poetry.setting

import android.app.Activity
import android.view.View
import com.libra.base.BaseBindingActivity
import com.libra.frame.api.Api
import com.libra.frame.api.DataManager
import com.libra.poetry.R
import com.libra.poetry.setting.xmlmodel.SettingViewModel
import com.libra.utils.startActivityForResult
import com.libra.utils.toast
import io.reactivex.functions.Consumer

class SettingActivity : BaseBindingActivity<com.libra.poetry.databinding.ActivitySettingBinding>() {
    companion object {
        fun start(activity: Activity) {
            activity.startActivityForResult<SettingActivity>(0x0001)
        }
    }

    private val xmlModel: SettingViewModel by lazy { SettingViewModel() }
    override fun getLayoutID(): Int {
        return R.layout.activity_setting
    }

    override fun initIntentData() {
    }

    override fun initXmlModel() {
        xmlModel.loginFlag.set(DataManager.instance.isLogin())
        xmlModel.questionClick = View.OnClickListener { }
        xmlModel.aboutPassClick = View.OnClickListener { }
        xmlModel.suggestClick = View.OnClickListener { }
        xmlModel.modifyPassClick = View.OnClickListener { }
        xmlModel.logoutClick = View.OnClickListener {
            showLoadingDialog()
            addObservable(
                Api.getInstance().logout()
                    .success(Consumer {
                        closeLoadingDialog()
                        setResult(Activity.RESULT_OK)
                        finish()
                    })
                    .error(Consumer {
                        closeLoadingDialog()
                        toast(it.message)
                    })
            )
        }
        binding?.xmlmodel = xmlModel
    }
}
