package com.libra.poetry.welcome

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.libra.base.BaseBindingActivity
import com.libra.frame.api.Api
import com.libra.frame.api.DataManager
import com.libra.login.LoginActivity
import com.libra.poetry.R
import com.libra.poetry.home.MainActivity
import io.reactivex.functions.Consumer

class WelcomeActivity : BaseBindingActivity<com.libra.poetry.databinding.ActivityWelcomeBinding>() {

    private var runTask: Runnable? = null
    private var start: Long = 0

    override fun getLayoutID(): Int = R.layout.activity_welcome

    override fun initIntentData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val params = window.attributes
            params.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or params.flags
        }
    }

    override fun initXmlModel() {
    }

    override fun initCustomView() {
        start = System.currentTimeMillis() / 1000
        getConfig()
    }

    private fun getConfig() {
        addObservable(Api.getInstance().getConfig().success(Consumer {
            val dvalue = System.currentTimeMillis() / 1000 - start
            if (dvalue >= 3) {
                goNext()
            } else {
                runTask = Runnable { goNext() }
                window.decorView.postDelayed(runTask, 1000 * (3 - dvalue))
            }
        }).error(Consumer {
            val dvalue = System.currentTimeMillis() / 1000 - start
            if (dvalue >= 3) {
                goNext()
            } else {
                runTask = Runnable { goNext() }
                window.decorView.postDelayed(runTask, 1000 * (3 - dvalue))
            }
        }))
    }

    private fun goNext() {
        if (DataManager.instance.isLogin()) {
            MainActivity.start(this)
            finish()
        } else {
            LoginActivity.start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x0001) {
                MainActivity.start(this)
                finish()
            }
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        if (runTask != null) {
            window.decorView.removeCallbacks(runTask)
        }
        super.onDestroy()
    }
}
