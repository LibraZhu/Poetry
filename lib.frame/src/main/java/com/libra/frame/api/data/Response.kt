package com.libra.frame.api.data

import com.google.gson.annotations.SerializedName
import com.libra.frame.Constants
import java.io.Serializable

/**
 * Created by libra on 2017/10/30.
 */

class Response<T> : Serializable {
    var code: Int = 0
    @SerializedName(value = "msg", alternate = ["message"])
    var msg: String? = null
    var data: T? = null

    val isSuccess: Boolean
        get() = code == Constants.ErrorCode.Success
}
