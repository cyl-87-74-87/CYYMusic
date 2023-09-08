package com.xiangxue.puremusic.bridge.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.xiangxue.puremusic.bridge.data.bean.AlbumModel
import com.xiangxue.puremusic.bridge.data.bean.DownloadFile
import com.xiangxue.puremusic.bridge.data.bean.LibraryInfo
import com.xiangxue.puremusic.data.bean.TestAlbum
import com.xiangxue.puremusic.data.bean.login_register.LoginRegisterResponse

/**
 * 远程请求标准接口（在仓库里面）
 * 只为 HttpRequestManager 服务
 */
interface IRemoteRequest {

    fun getFreeMusic(liveData: MutableLiveData<TestAlbum>?)
    fun getAlumMusic(liveData: MutableLiveData<List<AlbumModel>>?)

    fun getLibraryInfo(liveData: MutableLiveData<List<LibraryInfo>>?)

    // 下载文件
    fun downloadFile(liveData: MutableLiveData<DownloadFile>?)

    // 注册
    // 注册的标准
    fun register(
        context: Context,
        username: String,
        password: String,
        repassword: String,
        dataLiveData1: MutableLiveData<LoginRegisterResponse>,
        dataLiveData2: MutableLiveData<String>)

    // 登录
    // 登录的标准
    fun login(
        context: Context,
        username: String,
        password: String,
        dataLiveData1: MutableLiveData<LoginRegisterResponse>,
        dataLiveData2: MutableLiveData<String>)


    // 登录的标准-协程版本
    suspend fun loginCoroutine(
        username: String,
        password: String)
        : LoginRegisterResponse
}