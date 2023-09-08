package com.xiangxue.puremusic.test_code

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AActivity : AppCompatActivity() {

    // val iCallback  // 接口

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(LifeT1Manager.instance!!)

        // iCallback 关联接口
    }

    /*override fun onResume() {
        super.onResume()

        println("加载硬件信息数据成功...")
        isActive = true

        // 代码入侵
        iCallback 回调到 具体去做统一的 定位功能
    }

    override fun onPause() {
        super.onPause()

        // 业务代码
        println("销毁硬件信息数据成功...")
        isActive = false

        iCallback 回调到 具体去做统一的 定位功能
    }*/
}