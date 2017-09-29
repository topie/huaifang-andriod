package com.topie.huaifang.http

import com.topie.huaifang.http.bean.HFBaseResponseBody
import com.topie.huaifang.http.bean.communication.HFCommFriendsResponseBody
import com.topie.huaifang.http.bean.communication.HFCommMsgListResponseBody
import com.topie.huaifang.http.bean.function.*
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

    /**
     * 办事指南导航
     */
    @GET("/api/m/actionGuide/navs")
    fun getFunGuideMenu(): Observable<HFFunGuideMenuResponseBody>

    /**
     * 办事指南列表
     */
    @GET("/api/m/actionGuide/list")
    fun getFunGuideList(@Query("catId") catId: String, @Query("pageNum") pageNum: Int = 0, @Query("pageSize") pageSize: Int = 15): Observable<HFFunGuideListResponseBody>

    /**
     * 居务公开 导航
     */
    @GET("/api/m/juwuInfo/navs")
    fun getFunLiveMenu(): Observable<HFLiveMenuResponseBody>

    /**
     * 居务公开 列表
     */
    @GET("/api/m/juwuInfo/list")
    fun getFunLiveList(): Observable<HFLiveListResponseBody>

    /**
     * 居务公开 详情
     */
    @GET("/api/m/juwuInfo/list")
    fun getFunLiveDetail(@Query("id") id: String): Observable<HFLiveListResponseBody>

    /**
     * 通知公告 列表
     */
    @GET("/api/m/notice/list")
    fun getFunPublicList(@Query("type") type: Int, @Query("pageNum") pageNum: Int = 0, @Query("pageSize") pageSize: Int = 15): Observable<HFFunPublicResponseBody>

    /**
     * 通知公告 详情
     */
    @GET("/api/m/notice/detail")
    fun getFunPublicDetail(@Query("id") type: Int): Observable<HFFunPublicResponseBody>

    /**
     * 我的好友
     */
    @GET("/api/m/appUser/friends")
    fun getCommFriends(): Observable<HFCommFriendsResponseBody>

    /**
     * 社区党建，党建活动列表
     */
    @GET("/api/m/party/activity/list")
    fun getFunPartyActList(@Query("pageNum") pageNum: Int = 0, @Query("pageSize") pageSize: Int = 15): Observable<HFFunPartyResponseBody>

    /**
     * 党务公开
     */
    @GET("/api/m/party/activity/list")
    fun getFunPartyPublicList(): Observable<HFFunPartyPublicResponseBody>

    /**
     * 党员信息公开
     */
    @GET("/api/m/party/member/list")
    fun getFunPartyMemberList(): Observable<HFFunPartyMemberResponseBody>

    /**
     * 我的消息列表
     */
    @GET("/api/m/appMessage/list")
    fun getCommMsgList(): Observable<HFCommMsgListResponseBody>

    /**
     * 聊天室消息列表
     */
    @GET("/api/m/appUserMessage/list")
    fun getCommMsgDetail(): Observable<HFCommMsgListResponseBody>

    /**
     * 系统消息
     */
    @GET("/api/m/appMessage/detail")
    fun getCommMsgSystem(): Observable<HFCommMsgListResponseBody>
}
