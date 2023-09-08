package com.xiangxue.puremusic.data.bean.login_register

/**
 * 包装Bean

    {
        },
        "errorCode": 0,
        "errorMsg": ""
        }

    {
    "data": null,
    "errorCode": -1,
    "errorMsg": "账号密码不匹配！"
    }

 */
data class LoginRegisterResponseWrapper<T>(val data: T, val errorCode: Int, val errorMsg: String)