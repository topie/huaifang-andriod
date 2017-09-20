package com.topie.huaifang.http

import com.topie.huaifang.http.bean.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by arman on 2017/7/14.
 */

interface HFService {

    @POST("/api/token/login")
    @FormUrlEncoded
    fun login(@Field("username") username: String, @Field("password") password: String): Observable<Response<LoginResponseBody>>

    @GET("/api/m/noneAuth/unique")
    fun checkPhone(@Query("mobile") mobile: String): Observable<Response<CheckPhoneResponseBody>>

    @POST("/api/m/noneAuth/register")
    @FormUrlEncoded
    fun register(@Field("mobilePhone") mobilePhone: String, @Field("password") password: String): Observable<Response<BaseRequestBody>>
}
