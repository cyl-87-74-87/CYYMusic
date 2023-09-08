package com.cyymusic.puremusic.bridge.data.bean

/**
 * 音乐描述的实体类（显示在：例如：左侧半边白色）
 * 被 adapter_library.xml(DataBinding) 使用了
 * 被 InfoRequestViewModel 使用了
 * 被 HttpRequestManager 使用了
 * 被 IRemoteRequest接口 使用了
 * 被 IRemoteRequest接口 使用了
 * 被 DrawerFragment 使用了
 */
class LibraryInfo {

    var title // XiangxeMusic
            : String? = null

    var summary //JetPack项目”
            : String? = null

    var url // 本来是用来跳转到 WebView要加载的网页路径的
            : String? = null

    constructor() {}

    constructor(title: String?, summary: String?, url: String?) {
        this.title = title
        this.summary = summary
        this.url = url
    }
}