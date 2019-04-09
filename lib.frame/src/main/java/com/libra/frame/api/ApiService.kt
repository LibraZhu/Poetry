package com.libra.frame.api

import com.libra.frame.api.data.Config
import com.libra.frame.api.data.Response
import com.libra.frame.api.data.User
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by libra on 2017/10/30.F
 */

interface ApiService {

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/poetry/config/list")
    fun getConfig(@Body params: RequestBody): Flowable<Response<Config>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/poetry/user/register")
    fun register(@Body params: RequestBody): Flowable<Response<Any>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/poetry/user/forget_password")
    fun forgotPassword(@Body params: RequestBody): Flowable<Response<Any>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/poetry/user/send_sms_code")
    fun getcode(@Body params: RequestBody): Flowable<Response<Any>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/poetry/user/login")
    fun login(@Body params: RequestBody): Flowable<Response<User>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/poetry/user/logout")
    fun logout(@Body params: RequestBody): Flowable<Response<Any>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/poetry/user/info")
    fun userInfo(@Body params: RequestBody): Flowable<Response<User>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/poetry/user/modify_password")
    fun modifyPassword(@Body params: RequestBody): Flowable<Response<Any>>
}
