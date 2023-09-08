package com.xiangxue.puremusic.bridge.request

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xiangxue.puremusic.bridge.data.repository.HttpRequestManager
import com.xiangxue.puremusic.data.bean.login_register.LoginRegisterResponse

// 注册功能 请求服务器的 ViewModel
class RequestRegisterViewModel : ViewModel() {

    // 手写 模拟的  by lazy 懒加载功能（使用时 才会真正加载，这才是 懒加载）

    // 注册成功的状态 LiveData
    var registerData1 : MutableLiveData<LoginRegisterResponse> ? = null
    get() {
        if (field == null) {
            field = MutableLiveData()
        }
        return field
    }
    private set

    // 注册失败的状态 LiveData
    var registerData2 : MutableLiveData<String> ? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set

    fun requestRegister(context: Context, username: String, userpwd: String, reuserpwd: String) {
        // TODO
        // 可以做很多的事情
        // 可以省略很多代码
        // 很多的校验
        // ....

        // 没有任何问题后，直接调用仓库
        HttpRequestManager.instance.register(context, username, userpwd, reuserpwd, registerData1 !!, registerData2 !!)
    }

}