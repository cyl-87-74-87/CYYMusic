package com.cyymusic.puremusic.bridge.state

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

/**
 * 搜索Fragment 的 SearchViewModel
 * 状态VM是独一份，因为布局只有一份
 */
class SearchViewModel : ViewModel() {

    // LiveData  还是选择  DataBinding的ObservableField

    // 使用的是 DataBinding的ObservableField
    // 1.更新很频繁，因为要把进度更新到拖动条
    // 2.界面 可见 和 不可见，都必须执行，所以不能用 LiveData

    @JvmField // 取出getxx函数
    val progress = ObservableField<Int>()
}