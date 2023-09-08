package com.cyymusic.puremusic.bridge.state

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

/**
 * 首页Fragment 的 MainViewModel
 */
class MainViewModel : ViewModel() {

    // ObservableBoolean  or  LiveData
    // ObservableBoolean 防止抖动，频繁改变，节约运行内存，使用这个的场景
    // ObservableField<int 1000>
    // LiveData 反之

    // MainFragment初始化页面的标记 初始化选项卡和页面
    @JvmField // 剔除set
    val initTabAndPage = ObservableBoolean()

    // MainFragment “其他信息” 里面的 WebView需要加载的网页链接路径
    @JvmField // 剔除set
    val pageAssetPath = ObservableField<String>()

    // 登录信息的临时数据
    @JvmField
    val loginSessionInfo = ObservableField<String>()

}