package com.topie.huaifang.http

import com.topie.huaifang.http.bean.HFBaseResponseBody
import com.topie.huaifang.http.bean.function.HFFunGuideListResponseBody
import com.topie.huaifang.http.bean.function.HFFunGuideMenuResponseBody
import com.topie.huaifang.http.bean.login.HFCheckPhoneResponseBody
import com.topie.huaifang.http.bean.login.HFLoginResponseBody
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by arman on 2017/7/14.
 */

interface HFService {

    @POST("/api/token/login")
    @FormUrlEncoded
    fun login(@Field("username") username: String, @Field("password") password: String): Observable<HFLoginResponseBody>

    @GET("/api/m/noneAuth/unique")
    fun checkPhone(@Query("mobile") mobile: String): Observable<HFCheckPhoneResponseBody>

    @POST("/api/m/noneAuth/register")
    @FormUrlEncoded
    fun register(@Field("mobilePhone") mobilePhone: String, @Field("password") password: String): Observable<HFBaseResponseBody>

    @GET("/api/m/actionGuide/navs")
    fun getFunGuideMenu(): Observable<HFFunGuideMenuResponseBody>

    @GET("/api/m/notice/list")
    fun getFunGuideList(@Query("catId") catId: String, @Query("pageNum") pageNum: Int = 0, @Query("pageSize") pageSize: Int = 15): Observable<HFFunGuideListResponseBody>
}
