package com.cyymusic.puremusic.bridge.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.cyymusic.architecture.utils.Utils
import com.cyymusic.puremusic.R
import com.cyymusic.puremusic.bridge.data.bean.AlbumModel
import com.cyymusic.puremusic.bridge.data.bean.DownloadFile
import com.cyymusic.puremusic.bridge.data.bean.LibraryInfo
import com.cyymusic.puremusic.bridge.data.repository.api.WanAndroidAPI
import com.cyymusic.puremusic.bridge.data.repository.net.APIResponse
import com.cyymusic.puremusic.data.bean.TestAlbum
import com.cyymusic.puremusic.data.bean.login_register.LoginRegisterResponse
import com.cyymusic.puremusic.data.repository.net.APIClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

// 仓库角色
class HttpRequestManager private constructor() : ILoadRequest, IRemoteRequest {

    // 暂无使用到
    var responseCodeLiveData: MutableLiveData<String>? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set

    // 仓库：liveData: MutableLiveData<TestAlbum>?  已经和  Request VM 的 LiveData是 同一份了
    override fun getFreeMusic(liveData: MutableLiveData<TestAlbum>?) {
        val gson = Gson()
        val type = object : TypeToken<TestAlbum?>() {}.type
        val testAlbum =
            gson.fromJson<TestAlbum>(Utils.getApp().getString(R.string.free_music_json), type)

        // TODO 在这里可以请求网络
        // TODO 在这里可以请求网络
        // TODO 在这里可以请求数据库
        // .....

        // 子线程  协程  框架  liveData.postValue
        //设置数据
        liveData!!.value = testAlbum
    }
//new
    override fun getAlumMusic(liveData: MutableLiveData<List<AlbumModel>>?) {
        val gson = Gson()
        val type = object : TypeToken<List<AlbumModel?>?>() {}.type
        val albumModel =
            gson.fromJson<List<AlbumModel?>>(Utils.getApp().getString(R.string.albumMusic_json), type)
        liveData!!.value = albumModel as List<AlbumModel>?
    }

    override fun getLibraryInfo(liveData: MutableLiveData<List<LibraryInfo>>?) {
        val gson = Gson()
        val type = object : TypeToken<List<LibraryInfo?>?>() {}.type
        val list =
            gson.fromJson<List<LibraryInfo?>>(Utils.getApp().getString(R.string.library_json), type)

        liveData!!.value = list as List<LibraryInfo>?
    }

    /**
     * 搜索界面的时候讲
     * TODO：模拟下载任务:
     * 可分别用于 普通的请求，和可跟随页面生命周期叫停的请求，
     * 具体可见 ViewModel 和 UseCase 中的使用。
     *
     * @param liveData 从 Request-ViewModel 或 UseCase 注入 LiveData，用于 控制流程、回传进度、回传文件
     */
    override fun downloadFile(liveData: MutableLiveData<DownloadFile>?) {
        val timer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() {

                //模拟下载，假设下载一个文件要 10秒、每 100 毫秒下载 1% 并通知 UI 层
                var downloadFile = liveData!!.value
                if (downloadFile == null) {
                    downloadFile = DownloadFile()
                }
                if (downloadFile.progress < 100) {
                    downloadFile.progress = downloadFile.progress + 1
                    Log.d("TAG", "下载进度 " + downloadFile.progress + "%")
                } else {
                    timer.cancel()
                    downloadFile.progress = 0
                    return
                }
                if (downloadFile.isForgive) {
                    timer.cancel()
                    downloadFile.progress = 0
                    return
                }
                liveData.postValue(downloadFile)
                downloadFile(liveData)
            }
        }
        timer.schedule(task, 100)
    }

    // 注册的具体实现了
    override fun register(
        context: Context,
        username: String,
        password: String,
        repassword: String,
        dataLiveData1: MutableLiveData<LoginRegisterResponse>,
        dataLiveData2: MutableLiveData<String>) {

        // RxJava封装网络模型
        APIClient.instance.instanceRetrofit(WanAndroidAPI::class.java)
            .registerAction(username, password, repassword)

            .subscribeOn(Schedulers.io()) // 给上面的代码分配异步线程

            .observeOn(AndroidSchedulers.mainThread()) // 给下面的代码分配 安卓的主线程
            // dataLiveData1.postValue(data)

            .subscribe(object : APIResponse<LoginRegisterResponse>(context) {

                override fun success(data: LoginRegisterResponse?) { // RxJava自定义操作符过滤后的
                    // MVP 模式 各种接口回调
                    // callback.registerSuccess(data)

                    dataLiveData1.value = data // MVVM
                }

                override fun failure(errorMsg: String?) {  // RxJava自定义操作符过滤后的
                    dataLiveData2.value = errorMsg // MVVM
                }
            })
    }

    // 登录的具体实现了
    override fun login(
        context: Context,
        username: String,
        password: String,
        dataLiveData1: MutableLiveData<LoginRegisterResponse>,
        dataLiveData2: MutableLiveData<String>) {

        // RxJava封装网络模型
        APIClient.instance.instanceRetrofit(WanAndroidAPI::class.java)
            .loginAction(username, password)

            .subscribeOn(Schedulers.io()) // 给上面的代码分配异步线程

            .observeOn(AndroidSchedulers.mainThread()) // 给下面的代码分配 安卓的主线程
            // dataLiveData1.postValue(data)

            .subscribe(object : APIResponse<LoginRegisterResponse>(context) {

                override fun success(data: LoginRegisterResponse?) { // RxJava自定义操作符过滤后的
                    // MVP 模式 各种接口回调
                    // callback.loginSuccess(data)

                    dataLiveData1.value = data // MVVM
                }

                override fun failure(errorMsg: String?) {  // RxJava自定义操作符过滤后的
                    dataLiveData2.value = errorMsg // MVVM
                }
            })
    }


    // 登录的标准-协程版本-的具体代码
    override suspend fun loginCoroutine(
        username: String,
        password: String)
    =
        APIClient.instance.instanceRetrofit(WanAndroidAPI::class.java)
            .loginActionCoroutine(username, password).data


    companion object {
        val instance = HttpRequestManager()
    }
}