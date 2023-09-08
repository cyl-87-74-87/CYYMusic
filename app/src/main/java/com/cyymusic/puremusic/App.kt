package com.cyymusic.puremusic

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.cyymusic.architecture.utils.Utils
import com.cyymusic.puremusic.player.PlayerManager

/**
 * 整个项目的 Application
 */
class App : Application(), ViewModelStoreOwner {

    private var mAppViewModelStore: ViewModelStore? = null
    private var mFactory: ViewModelProvider.Factory? = null

    override fun onCreate() {
        super.onCreate()

        Utils.init(this)
        mAppViewModelStore = ViewModelStore() // 此处 只会调用一次

        // 同学们，这里必须初始化一下，是为了保证播放音乐管理类（PlayerManager.java） 不会为null，从而不引发空指针异常
        PlayerManager.instance.init(this)
    }

    // 关键函数，只暴露 给 BaseActivity 与 BaseFragment 用的，保证共享ViewModel初始化的 单例
    // 专门给 BaseActivity 与 BaseFragment 用的
    fun getAppViewModelProvider(activity: Activity): ViewModelProvider {
        return ViewModelProvider(
            (activity.applicationContext as App),
            (activity.applicationContext as App).getAppFactory(activity) !!
        )
    }

    // AndroidViewModelFactory 工程是为了创建ViewModel，给上面的getAppViewModelProvider函数用的
    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory? {
        val application = checkApplication(activity)
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
        return mFactory
    }

    // 监测下 Activity是否为null
    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    // 监测下 Activity是否为null
    private fun checkActivity(fragment: Fragment): Activity? {
        return fragment.activity
            ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }

    // TODO 暴露出去 给外界用
    // 同学们注意，此函数只给 NavHostFragment 使用
    override fun getViewModelStore(): ViewModelStore = mAppViewModelStore !!

}