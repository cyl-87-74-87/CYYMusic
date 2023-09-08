package com.xiangxue.puremusic.test_code

class T1Manager {
    var isActive = false
    fun onResumt() {
        println("加载硬件信息数据成功...")
    }

    fun onPause() {
        println("销毁硬件信息数据成功...")
    }

    companion object {
        var t1Manager: T1Manager? = null
        val instance: T1Manager?
            get() {
                if (t1Manager == null) {
                    t1Manager = T1Manager()
                }
                return t1Manager
            }
    }
}