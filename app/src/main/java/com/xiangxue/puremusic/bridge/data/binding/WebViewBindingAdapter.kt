package com.xiangxue.puremusic.bridge.data.binding

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BindingAdapter
import com.xiangxue.architecture.utils.Utils

/**
 * TODO 学一直编译不通过，各种错误，真正的原因是在这里哦，这里和布局建立绑定的呢
 *
 * 展示WebView的适配器
 * 各项参数设置
 *
 * 注意：这个类的使用，居然是和fragment_main.xml里面的pageAssetPath / loadPage挂钩的
 */
object WebViewBindingAdapter {

    /**
     * 加载WebView，固定加载Assets目录下的资源
     */
    @JvmStatic
    @SuppressLint("SetJavaScriptEnabled")
    @BindingAdapter(value = ["pageAssetPath"], requireAll = false)
    fun loadAssetsPage(webView: WebView, assetPath: String) {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val uri = request.url
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                Utils.getApp().startActivity(intent)
                return true
            }
        }
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.defaultTextEncodingName = "UTF-8"
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        val url = "file:///android_asset/$assetPath"
        webView.loadUrl(url)
    }

    /**
     * 加载WebView，灵活加载用户传递进来的 loadPage 路径资源
     */
    @SuppressLint("SetJavaScriptEnabled")
    @BindingAdapter(value = ["loadPage"], requireAll = false)
    fun loadPage(webView: WebView, loadPage: String?) {
        webView.webViewClient = WebViewClient()
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.defaultTextEncodingName = "UTF-8"
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webView.loadUrl(loadPage)
    }
}