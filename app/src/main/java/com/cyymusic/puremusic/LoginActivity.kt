package com.cyymusic.puremusic

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.cyymusic.puremusic.bridge.data.login_register.Session
import com.cyymusic.puremusic.bridge.request.RequestLoginViewModel
import com.cyymusic.puremusic.bridge.state.LoginViewModel
import com.cyymusic.puremusic.data.bean.login_register.LoginRegisterResponse
import com.cyymusic.puremusic.databinding.ActivityUserLoginBinding
import com.cyymusic.puremusic.ui.base.BaseActivity

// 登录功能的Activity
class LoginActivity : BaseActivity() {

    var mainBinding: ActivityUserLoginBinding? = null // 当前Register的布局
    var loginViewModel: LoginViewModel? = null // ViewModel

    var requestLoginViewModel : RequestLoginViewModel? = null // TODO Reqeust ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this@LoginActivity,  MainActivity::class.java))
        hideActionBar()

        loginViewModel = getActivityViewModelProvider(this).get(LoginViewModel::class.java) // State ViewModel初始化
        requestLoginViewModel = getActivityViewModelProvider(this).get(RequestLoginViewModel::class.java) // Request ViewModel初始化
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_login) // DataBinding初始化
        mainBinding ?.lifecycleOwner = this
        mainBinding ?.vm = loginViewModel // 绑定ViewModel与DataBinding关联
        mainBinding ?.click = ClickClass() // DataBinding关联 的点击事件

        // 登录成功 眼睛监听 成功
        requestLoginViewModel ?.registerData1 ?.observe(this) {
            loginSuccess(it!!)
        }

        // 登录失败 眼睛监听 失败
        requestLoginViewModel ?.registerData2 ?.observe(this) {
            loginFialure(it!!)
        }
    }

    // 响应的两个函数
    fun loginSuccess(registerBean: LoginRegisterResponse?) {
        //  Toast.makeText(this@LoginActivity, "登录成功😀", Toast.LENGTH_SHORT).show()
        loginViewModel?.loginState?.value = "恭喜 ${registerBean?.username} 用户，登录成功"

        // 登录成功 在跳转首页之前，需要 保存 登录的会话
        // 保存登录的临时会话信息
        mSharedViewModel.session.value = Session(true, registerBean)

        // 跳转到首页
        startActivity(Intent(this@LoginActivity,  MainActivity::class.java))
    }

    fun loginFialure(errorMsg: String?) {
        // Toast.makeText(this@LoginActivity, "登录失败 ~ 呜呜呜", Toast.LENGTH_SHORT).show()
        loginViewModel ?.loginState ?.value = "骚瑞 登录失败，错误信息是:${errorMsg}"
    }

    inner class ClickClass {

        // 点击事件，登录的函数
        fun loginAction() {
            if (loginViewModel !!.userName.value.isNullOrBlank() || loginViewModel !!.userPwd.value.isNullOrBlank()) {
                loginViewModel ?.loginState ?.value = "用户名 或 密码 为空，请你好好检查"
                return
            }

            // 非协程版本
            /*requestLoginViewModel ?.requestLogin(
                this@LoginActivity,
                loginViewModel !!.userName.value!!,
                loginViewModel !!.userPwd.value!!,
                loginViewModel !!.userPwd.value!!
            )*/

            // 协程版本
            requestLoginViewModel ?.requestLoginCoroutine( this@LoginActivity, loginViewModel !!.userName.value!!, loginViewModel !!.userPwd.value!!)
        }

        // 跳转到 注册界面
        fun startToRegister() = startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
    }
}