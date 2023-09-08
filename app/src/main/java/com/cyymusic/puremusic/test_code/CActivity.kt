package com.cyymusic.puremusic.test_code

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(LifeT1Manager.instance!!)
    }

    /*override fun onResume() {
        super.onResume()

        println("加载硬件信息数据成功...")
        isActive = true
    }

    override fun onPause() {
        super.onPause()

        // 业务代码
        println("销毁硬件信息数据成功...")
        isActive = false
    }*/
}