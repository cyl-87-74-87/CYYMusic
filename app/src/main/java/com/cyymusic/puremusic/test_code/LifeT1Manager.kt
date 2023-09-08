package com.cyymusic.puremusic.test_code

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class LifeT1Manager : DefaultLifecycleObserver {

    var isActive = false
    override fun onResume(owner: LifecycleOwner) {
        // 业务代码
        println("加载硬件信息数据成功...")
        isActive = true
    }

    override fun onPause(owner: LifecycleOwner) {
        // 业务代码
        println("销毁硬件信息数据成功...")
        isActive = false
    }

    companion object {
        private var t1Manager: LifeT1Manager? = null
        val instance: LifeT1Manager?
            get() {
                if (t1Manager == null) {
                    t1Manager = LifeT1Manager()
                }
                return t1Manager
            }
    }
}