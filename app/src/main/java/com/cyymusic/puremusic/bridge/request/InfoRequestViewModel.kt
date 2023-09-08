package com.cyymusic.puremusic.bridge.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyymusic.puremusic.bridge.data.bean.LibraryInfo
import com.cyymusic.puremusic.bridge.data.repository.HttpRequestManager

/**
 * 抽屉的 左侧半界面 要使用的 ViewModel
 */
class InfoRequestViewModel : ViewModel() {

    var libraryLiveData: MutableLiveData<List<LibraryInfo>>? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set

    fun requestLibraryInfo() {
        // 请求服务器，协程  RxJava
        // 调用仓库
        HttpRequestManager.instance.getLibraryInfo(libraryLiveData)
    }
}