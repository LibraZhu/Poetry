package com.libra.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.libra.base.BaseBindingActivity
import com.libra.frame.utils.StatusBarLight
import com.libra.utils.startActivity
import com.libra.utils.startActivityForResult
import com.libra.utils.toast

class WebActivity : BaseBindingActivity<com.libra.login.databinding.ActivityWebBinding>() {
    companion object {
        val PARAM_URL = "param_url"
        val PARAM_TITLE = "param_title"
        val PARAM_ZMXY = "param_zmxy"
        fun start(activity: Activity, title: String, url: String?) {
            activity.startActivity<WebActivity>(PARAM_URL to url, PARAM_TITLE to title)
        }


        fun startForResult(activity: Activity, title: String, url: String?) {
            activity.startActivityForResult<WebActivity>(0x0001, PARAM_URL to url,
                    PARAM_TITLE to title)
        }
    }

    private var mAgentWeb: AgentWeb? = null
    private var url = ""
    private var mTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarLight.light(window)
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_web
    }

    override fun initIntentData() {
        url = intent.getStringExtra(PARAM_URL) ?: ""
        mTitle = intent.getStringExtra(PARAM_TITLE) ?: ""
        title = mTitle
        if (TextUtils.isEmpty(url)) {
            toast("无效链接")
            return
        }
    }

    override fun initXmlModel() {
    }

    override fun initCustomView() {
        val preAgentWeb = AgentWeb.with(this) //传入Activity or Fragment
                .setAgentWebParent(binding?.container!!, LinearLayout.LayoutParams(-1,
                        -1)) //传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator() // 使用默认进度条
                .setWebViewClient(object : WebViewClient() {

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        if (true == url?.startsWith("alipays")) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            } catch (e: Exception) {
                                toast("打不开支付宝,检测是否安装")
                                e.printStackTrace()
                            }
                        } else {
                            mAgentWeb?.urlLoader?.loadUrl(url)
                        }
                        return true
                    }
                })
                .setWebChromeClient(object : WebChromeClient() {
                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        if (TextUtils.isEmpty(mTitle)) {
                            setTitle(title)
                        }
                    }
                })
                .createAgentWeb() //
                .ready()
        if (url.toUpperCase().startsWith("HTTP")) {
            mAgentWeb = preAgentWeb.go(url)
        } else {
            mAgentWeb = preAgentWeb.go("")
            mAgentWeb?.webCreator?.webView?.loadDataWithBaseURL("", url, "text/html", "utf-8", null)
        }
        mAgentWeb?.jsInterfaceHolder?.addJavaObject("sirenguanjia", this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()

    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        super.onDestroy()
    }

    @JavascriptInterface
    fun goback() {
        Handler().post {
            finish()
        }
    }
}
