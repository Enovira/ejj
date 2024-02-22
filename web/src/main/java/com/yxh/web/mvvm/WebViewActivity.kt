package com.yxh.web.mvvm

import android.os.Bundle
import android.util.DisplayMetrics
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.yxh.web.R
import com.yxh.web.core.utils.AndroidBug5497Workaround
import com.yxh.web.databinding.ActivityWebviewBinding

class WebViewActivity : BaseActivity<EmptyViewModel, ActivityWebviewBinding>() {

    private var _webView: WebView? = null
    private val webView get() = _webView!!
    private lateinit var rootView: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_webview
    }

    override fun initView() {
        rootView = binding.rootView
        _webView = binding.webView

        intent?.getStringExtra("url")?.let {
            if (!it.startsWith("http")) {
                Toast.makeText(this, "不是有效的url链接", Toast.LENGTH_SHORT).show()
                Thread {
                    Thread.sleep(2000)
                    runOnUiThread {
                        finish()
                    }
                }.start()
                return@let
            }
            webView.apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }
                webViewClient = CustomWebViewClient()
//                loadUrl("http://192.168.1.229:8080")
//                loadUrl("http://192.168.1.135:8080")
//                loadUrl("file:///android_asset/index.html")
//                loadUrl("http://27.155.120.43:8080/")
                loadUrl(it)
            }
        } ?: kotlin.run {
            Toast.makeText(this, "不是有效url", Toast.LENGTH_SHORT).show()
        }


    }

    private inner class CustomWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
//            try {
//                Thread {
//                    Thread.sleep(5000) // 过五秒再过去焦点及自动填入
//                    webView.loadUrl("javascript:autoFill()")
//                }.start()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rootView.removeView(webView)// 将webView从父布局上移除
        webView.clearCache(true) // 清除WebView的缓存
        webView.clearHistory() // 清除WebView的历史记录
        webView.removeAllViews() // WebView移除所有子View
        webView.destroy() // 删除WebView
        _webView = null // 置空
    }

    private fun scaleScreenSize() {
        val dm = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(dm)
        val screenWidth = dm.widthPixels
        val screenHeight = dm.heightPixels
        println("屏幕分辨率 = $screenWidth*$screenHeight")
        println("dm.density = " + dm.density + "," + "dm.densityDpi = " + dm.densityDpi)
    }
}