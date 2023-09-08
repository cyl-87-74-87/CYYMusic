package com.cyymusic.puremusic.data.bean.login_register


/**
 * data 登录成功 需要把这个Bean 给 UI

 */

// 通配符Java?   ====   kt *
// String ? 允许服务器字段是 null
data class LoginRegisterResponse(val admin: Boolean,
                                 val chapterTops: List<*>,
                                 val collectIds: List<*>,
                                 val email: String ?,
                                 val icon: String?,
                                 val id: String?,
                                 val nickname: String?,
                                 val password: String?,
                                 val publicName: String?,
                                 val token: String?,
                                 val type: Int,
                                 val username: String?
                         )