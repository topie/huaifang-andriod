package com.topie.huaifang.http

import com.topie.huaifang.http.bean.LoginRequestBody
import com.topie.huaifang.http.bean.LoginResponseBody
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by arman on 2017/7/14.
 */

interface HFService {

    @POST("/api/token/login")
    fun login(@Body loginRequestBody: LoginRequestBody): Observable<Response<LoginResponseBody>>
}