package com.libra.frame.api.data

/**
 * Created by libra on 2017/10/30.
 */

data class User(
    var id: Long = 0, var account: String? = null, var avatar: String? = null,
    var birthday: Int = 0, var createTime: Int = 0, var email: Int = 0, var ip: Int = 0,
    var nickname: Int = 0, var phone: Int = 0, var sex: String? = null, var status: String? = null,
    var token: String? = null, var updateTime: String? = null
) {
}
