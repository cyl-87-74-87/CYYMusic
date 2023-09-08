package com.cyymusic.puremusic.ui.base

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.cyymusic.puremusic.bridge.callback.SharedViewModel
import com.cyymusic.puremusic.App

/**
 * 所有Fragment的父类
 */
open class BaseFragment : Fragment()  {

    protected var mActivity: AppCompatActivity? = null // 为了 让所有的子类 持有 Activity
    // private var _sharedViewModel: SharedViewModel
    // 贯穿整个项目的（只会让App(Application)初始化一次）
    protected lateinit var sharedViewModel: SharedViewModel // 共享区域的ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = getAppViewModelProvider().get(SharedViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    // 同学们，测试用的，暂无用
    fun isDebug(): Boolean {
        return mActivity!!.applicationContext.applicationInfo != null &&
                mActivity!!.applicationContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    // 同学们，只是提示而已
    fun showLongToast(text: String?) {
        Toast.makeText(mActivity!!.applicationContext, text, Toast.LENGTH_LONG).show()
    }

    // 同学们，只是提示而已
    fun showShortToast(text: String?) {
        Toast.makeText(mActivity!!.applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    // 同学们，只是提示而已
    fun showLongToast(stringRes: Int) {
        showLongToast(mActivity!!.applicationContext.getString(stringRes))
    }

    // 同学们，只是提示而已
    fun showShortToast(stringRes: Int) {
        showShortToast(mActivity!!.applicationContext.getString(stringRes))
    }

    // 给当前BaseFragment用的【共享区域的ViewModel】
    protected fun getAppViewModelProvider(): ViewModelProvider {
        return (mActivity!!.applicationContext as App).getAppViewModelProvider(mActivity!!)
    }

    // 给所有的 子fragment提供的函数，可以顺利的拿到 ViewModel 【非共享区域的ViewModel】
    protected fun getFragmentViewModelProvider(fragment: Fragment): ViewModelProvider {
        return ViewModelProvider(fragment, fragment.defaultViewModelProviderFactory)
    }

    // 备用的
    // 给所有的 子fragment提供的函数，可以顺利的拿到 ViewModel 【非共享区域的ViewModel】
    protected fun getActivityViewModelProvider(activity: AppCompatActivity): ViewModelProvider {
        return ViewModelProvider(activity, activity.defaultViewModelProviderFactory)
    }

    /**
     * 为了给所有的 子fragment，导航跳转fragment的
     * @return
     */
    protected fun nav(): NavController {
        return NavHostFragment.findNavController(this)
    }

    /**
     * 对外暴露 SharedViewModel，所有的 子fragment都可以使用 共享的SharedViewModel
     * @return
     */
    /*fun getSharedViewModel(): SharedViewModel {
        return sharedViewModel
    }*/
}