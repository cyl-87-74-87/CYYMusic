package com.xiangxue.puremusic.bridge.request

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xiangxue.puremusic.bridge.data.repository.HttpRequestManager
import com.xiangxue.puremusic.data.bean.login_register.LoginRegisterResponse
import com.xiangxue.puremusic.ui.view.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 请求登录的ViewModel
class RequestLoginViewModel : ViewModel() {

    var registerData1: MutableLiveData<LoginRegisterResponse>? = null
        get() {
            if (field == null) {
                field = MutableLiveData<LoginRegisterResponse>()
            }
            return field
        }
        private set

    var registerData2: MutableLiveData<String>? = null
        get() {
            if (field == null) {
                field = MutableLiveData<String>()
            }
            return field
        }
        private set

    // 非协程函数
    fun requestLogin(context: Context, username: String, userpwd: String, reuserpwd: String) {
        // TODO
        // 可以做很多的事情
        // 可以省略很多代码
        // 很多的校验
        // ....

        HttpRequestManager.instance.login(context, username, userpwd, registerData1!!, registerData2!!)
    }


    // 协程函数
    fun requestLoginCoroutine(context: Context, username: String, userpwd: String) {
        // TODO
        // 可以做很多的事情
        // 可以省略很多代码
        // 很多的校验
        // ....

        // GlobalScope(Dispatchers.Main) 全局作用域 默认是异步线程
        // viewModelScope.launch 默认是主线程 == (Dispatchers.Main)
        viewModelScope.launch {
            // 当前是主线程，可以弹框
            LoadingDialog.show(context)

            // 思考：为什么不能这样写？
            // 左边的是： 主线程              右边：异步线程
            // registerData1 ?.value   =    HttpRequestManager.instance.loginCoroutine(username, userpwd)

            // 左边的是： 主线程              右边：异步线程
            val dataResult =  HttpRequestManager.instance.loginCoroutine(username, userpwd)

            // 当前是主线程，可以用 setValue更新 状态
            if (dataResult != null) {
                registerData1 ?.value  = dataResult
            } else {
                registerData2 ?.value = "登录失败，发送了意外，请检查用户名与密码 或者 你的垃圾代码"
            }

            // 当前是主线程，可以 取消弹框
            LoadingDialog.cancel()
        }
    }
}