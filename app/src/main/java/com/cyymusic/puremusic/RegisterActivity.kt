package com.cyymusic.puremusic

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.cyymusic.puremusic.bridge.request.RequestRegisterViewModel
import com.cyymusic.puremusic.bridge.state.RegisterViewModel
import com.cyymusic.puremusic.data.bean.login_register.LoginRegisterResponse
import com.cyymusic.puremusic.databinding.ActivityUserRegisterBinding
import com.cyymusic.puremusic.ui.base.BaseActivity

// 注册功能的Activity
class RegisterActivity : BaseActivity() {

    var mainBinding : ActivityUserRegisterBinding ? = null // 当前Register的布局
    var registerViewModel: RegisterViewModel? = null // ViewModel

    var requestRegisterViewModel : RequestRegisterViewModel? = null // TODO Reqeust ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideActionBar()

        registerViewModel = getActivityViewModelProvider(this).get(RegisterViewModel::class.java) // 状态VM
        requestRegisterViewModel = getActivityViewModelProvider(this).get(RequestRegisterViewModel::class.java) // 请求VM
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_register) // 初始化DB
        mainBinding ?.lifecycleOwner = this
        mainBinding ?.vm = registerViewModel // DataBinding绑定 ViewModel
        mainBinding ?.click = ClickClass() // 布局建立点击事件

        // 一双眼睛 盯着 requestRegisterViewModel 监听 成功了吗
        requestRegisterViewModel ?.registerData1 ?.observe(this) {
            registerSuccess(it)
        }

        // 一双眼睛 盯着 requestRegisterViewModel 监听 失败了吗
        requestRegisterViewModel ?.registerData2 ?.observe(this) {
            registerFailed(it)
        }
    }

    fun registerSuccess(registerBean: LoginRegisterResponse?) {
        // Toast.makeText(this, "注册成功😀", Toast.LENGTH_SHORT).show()
        registerViewModel ?.registerState ?.value = "恭喜 ${registerBean ?.username} 用户，注册成功"

        // TODO 注册成功，直接进入的登录界面  同学们去写
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun registerFailed(errorMsg: String?) {
        // Toast.makeText(this, "注册失败 ~ 呜呜呜", Toast.LENGTH_SHORT).show()
        registerViewModel ?.registerState ?.value = "骚瑞 注册失败，错误信息是:${errorMsg}"
    }

    inner class ClickClass {

        // 点击事件，注册的函数
        fun registerAction() {
            if (registerViewModel !!.userName.value.isNullOrBlank() || registerViewModel !!.userPwd.value.isNullOrBlank()) {
                registerViewModel ?.registerState ?.value = "用户名 或 密码 为空，请你好好检查"
                return
            }

            requestRegisterViewModel ?.requestRegister(
                this@RegisterActivity,
                        registerViewModel !!.userName.value !!,
                        registerViewModel !!.userPwd.value !!,
                        registerViewModel !!.userPwd.value !!
            )
        }
    }
}