package com.xiangxue.puremusic.bridge.data.bean

import java.io.File

/**
 * 下载文件的实体Bena封装
 *
 * 被 DownloadViewModel使用
 * 被 IRemoteRequest 接口使用
 * 被 HttpRequestManager （模拟下载使用）
 * 被 CanBeStoppedUseCase 使用了，具体细节还需要看研究
 */
class DownloadFile {
    var progress = 0
    var file: File? = null
    var isForgive = false

    constructor() {}

    constructor(progress: Int, file: File?, forgive: Boolean) {
        this.progress = progress
        this.file = file
        isForgive = forgive
    }
}