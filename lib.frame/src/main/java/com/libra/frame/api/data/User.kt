package com.libra.frame.api.data

/**
 * Created by libra on 2017/10/30.
 */

data class User(
    var id: Long = 0, var account: String? = null, var avatar: String? = null,
    var birthday: String? = null, var createTime: String? = null, var email: String? = null, var ip: String? = null,
    var nickname: String? = null, var phone: String? = null, var sex: String? = null, var status: String? = null,
    var token: String? = null, var updateTime: String? = null, var fans: Int = 0, var follow: Int = 0
) {
}
