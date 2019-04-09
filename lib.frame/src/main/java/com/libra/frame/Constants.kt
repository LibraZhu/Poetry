package com.libra.frame

/**
 * Created by libra on 2017/10/30.
 */

interface Constants {
    companion object {
        val PageSize = 10
    }

    interface IntentParam {
        companion object {
            val Tag = "tag"
            val Index = "index"
        }
    }

    interface ValidateCode {
        companion object {
            val Register = 1
            val Login = 2
            val ForgotPassword = 3
        }
    }

    interface ErrorCode {
        companion object {
            const val Success = 10000
            const val Error_Token = 10001
        }
    }
}
