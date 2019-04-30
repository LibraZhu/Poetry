package com.libra.poetry.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.libra.base.BaseBindingActivity
import com.libra.base.BaseBindingFragment
import com.libra.frame.utils.StatusBarLight
import com.libra.login.LoginActivity
import com.libra.poetry.R
import com.libra.utils.startActivity
import com.libra.utils.toast
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseBindingActivity<com.libra.poetry.databinding.ActivityMainBinding>() {
    companion object {
        fun start(activity: Activity) {
            activity.startActivity<MainActivity>()
        }
    }

    private var fragmentList: ArrayList<BaseBindingFragment<*>> = ArrayList()
    override fun getLayoutID(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        StatusBarLight.light(window)
        super.onCreate(savedInstanceState)
    }

    override fun initToolBar() {
        StatusBarLight.lightFullScreen(window)
        super.initToolBar()
    }

    override fun initIntentData() {
        permissions()
    }

    private fun permissions() {
        val dis = RxPermissions(this)
            .request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
            )
            .subscribe(
                { p ->
                    when {
                        p -> {
                        }
                        else -> { // Denied permission with ask never again
                            // Need to go to the settings
                            Logger.e(
                                "Denied permission without ask never again,Need to go to the settings"
                            )
                            toast("Denied permission without ask never again,Need to go to the settings")
                            try {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                ).setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                ).setData(
                                    Uri.parse(
                                        "package:$packageName"
                                    )
                                )
                                startActivity(
                                    intent
                                )
                            } catch (e: Exception) {

                            }
                        }
                    }
                },
                { e ->
                    Logger.e(
                        e?.message ?: ""
                    )
                })
    }

    override fun initXmlModel() {
    }

    override fun initCustomView() {
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_home -> {
                    viewpager.currentItem = 0
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab_repay -> {
                    viewpager.currentItem = 1
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab_user -> {
                    viewpager.currentItem = 2
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
        initFragment()
    }

    private fun initFragment() {
        fragmentList.add(PoetryFragment.newInstance())
        fragmentList.add(GroupFragment.newInstance())
        fragmentList.add(UserFragment.newInstance())
        viewpager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }

            override fun getCount(): Int {
                return fragmentList.size
            }
        }
        viewpager.offscreenPageLimit = 3
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x0001) {
                LoginActivity.start(this) {
                    start(it)
                    it.finish()
                }
                finish()
            }
        }
    }
}
