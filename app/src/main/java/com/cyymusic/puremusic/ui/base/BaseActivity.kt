package com.cyymusic.puremusic.ui.base

import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cyymusic.puremusic.bridge.callback.SharedViewModel
import com.cyymusic.architecture.data.manager.NetworkStateManager
import com.cyymusic.architecture.utils.AdaptScreenUtils
import com.cyymusic.architecture.utils.BarUtils
import com.cyymusic.architecture.utils.ScreenUtils
import com.cyymusic.puremusic.App

/**
 * 所有Activity 的基类
 */
// open 剔除 final修饰
open class BaseActivity : AppCompatActivity() {

    // 贯穿整个项目的（只会让App(Application)初始化一次）
    protected lateinit var mSharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 需要用到工具
        // 给工具类初始化
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
        BarUtils.setStatusBarLightMode(this, true)

        mSharedViewModel = getAppViewModelProvider().get<SharedViewModel>(SharedViewModel::class.java)

        // 准备：lifecycle
        // 意味着 BaseActivity被观察者  -----> NetworkStateManager观察者（一双眼睛 盯着看 onResume/onPause）
        // BaseActivity就是被观察者 ---> NetworkStateManager.getInstance()
        lifecycle.addObserver(NetworkStateManager.instance)
    }

    // 工具函数
    /**
     * 暴漏给自己的孩子   隐藏ActionBar
     */
    fun hideActionBar() {
        // 任何 Java代码东西，必须用 ？ 允许为null，来接收
        val actionBar : ActionBar? = supportActionBar
        actionBar?.hide()
    }

    fun isDebug(): Boolean {
        return applicationContext.applicationInfo != null &&
                applicationContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    // BaseActivity的 Resource信息给 暴露给外界用
    override fun getResources(): Resources? {
        return if (ScreenUtils.isPortrait) {
            AdaptScreenUtils.adaptWidth(super.getResources(), 360)
        } else {
            AdaptScreenUtils.adaptHeight(super.getResources(), 640)
        }
    }

    // 工具函数 提示Toast而已
    fun showLongToast(text: String?) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    // 工具函数 提示Toast而已
    fun showShortToast(text: String?) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    // 2020 用法 ViewModelProvider 【ViewModel共享区域】
    // 此getAppViewModelProvider函数，只给 共享的ViewModel用（例如：mSharedViewModel .... 等共享的ViewModel）
    protected fun getAppViewModelProvider(): ViewModelProvider {
        return (applicationContext as App).getAppViewModelProvider(this)
    }

    // 此getActivityViewModelProvider函数，给所有的 BaseActivity 子类用的 【ViewModel非共享区域】
    protected fun getActivityViewModelProvider(activity: AppCompatActivity): ViewModelProvider {
        return ViewModelProvider(activity, activity.defaultViewModelProviderFactory)
    }
}