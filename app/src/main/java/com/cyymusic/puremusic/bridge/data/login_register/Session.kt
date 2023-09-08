package com.cyymusic.puremusic.bridge.data.login_register

import com.cyymusic.puremusic.data.bean.login_register.LoginRegisterResponse

// 保存登录信息的临时会话
data class Session constructor(val isLogin: Boolean, val loginRegisterResponse: LoginRegisterResponse?)