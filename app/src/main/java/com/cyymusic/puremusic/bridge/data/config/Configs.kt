package com.cyymusic.puremusic.bridge.data.config

import android.os.Environment
import com.cyymusic.architecture.utils.Utils

object Configs {

    // 封面路径地址
    @JvmField
    val COVER_PATH = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath

    const val TAG = "Derry"
}