package com.peakchao.webapp

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSetting()

        if (BuildConfig.isLocal) {
            mWebView.loadUrl("file:///android_asset/index.html");
        } else {
            mWebView.loadUrl(BuildConfig.web_url);
        }
    }

    private fun initSetting() {
        mWebProgress.max = 100
        val settings = mWebView.settings
        mWebView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        settings.defaultTextEncodingName = "UTF-8"
        //设置自适应屏幕，两者合用
        settings.allowFileAccess = true //设置可以访问文件
        settings.loadsImagesAutomatically = true //支持自动加载图片
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.builtInZoomControls = false
        settings.setSupportMultipleWindows(true)
        settings.useWideViewPort = true//将图片调整到适合webview的大小
        settings.loadWithOverviewMode = true// 缩放至屏幕的大小
        settings.setSupportZoom(false)
        settings.pluginState = WebSettings.PluginState.ON
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
        settings.javaScriptEnabled = true
        //自动播放音乐
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.mediaPlaybackRequiresUserGesture = false
        }
        if (isNetAvailable(this)) {
            settings.cacheMode = WebSettings.LOAD_DEFAULT//根据cache-control决定是否从网络上取数据。
        } else {
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK//没网，则从本地获取，即离线加载
        }
        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
            }

            override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }
        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    mWebProgress.visibility = View.GONE
                } else {
                    if (mWebProgress.visibility == View.GONE) {
                        mWebProgress.visibility = View.VISIBLE
                    }
                    mWebProgress.setProgress(newProgress)
                }
            }
        }
    }


    private fun isNetAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        return info != null && info.isAvailable
    }


    public override fun onResume() {
        if (mWebView != null) {
            mWebView.onResume()
        }
        super.onResume()
    }

    public override fun onPause() {
        if (mWebView != null) {
            mWebView.onPause()
            mWebView.stopLoading()
        }
        super.onPause()
    }

    public override fun onDestroy() {
        if (mWebView != null) {
            mWebView.destroy()
        }
        super.onDestroy()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        return false
    }
}
