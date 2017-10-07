package com.topie.huaifang.http

import com.topie.huaifang.http.bean.HFBaseResponseBody
import com.topie.huaifang.http.bean.communication.HFCommFriendsResponseBody
import com.topie.huaifang.http.bean.communication.HFCommMsgListResponseBody
import com.topie.huaifang.http.bean.function.*
import com.topie.huaifang.http.bean.login.HFCheckPhoneResponseBody
import com.topie.huaifang.http.bean.login.HFLoginResponseBody
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File


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

    @GET("/api/m/activity/join")
    fun postFunPartyAct(@Query("id") id: Int): Observable<HFBaseResponseBody>

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

    /**
     * 迷你黄页
     */
    @GET("/api/m/yellowPage/list")
    fun getFunYellowPage(): Observable<HFFunYellowPageResponseBody>

    /**
     * 调查问卷列表
     */
    @GET("/api/m/question/list")
    fun getFunQuestionList(): Observable<HFFunQuestionResponseBody>


    /**
     * 可能认识的人
     */
    @GET("/api/m/appUser/maybeKnown")
    fun getCommSimilarFriend(): Observable<HFCommFriendsResponseBody>

    /**
     * 添加好友
     */
    @GET("/api/m/appUser/addFriend")
    fun addCommFriend(@Query("id") id: Int): Observable<HFBaseResponseBody>

    @GET("/api/logout")
    fun logout(): Observable<HFBaseResponseBody>

    /**
     * 社区图书馆，导航
     */
    @GET("/api/m/libraryBook/category")
    fun getFunLibraryMenus(): Observable<HFFunLibraryMenuResponseBody>


    /**
     * 社区图书馆，列表
     */
    @GET("/api/m/libraryBook/list")
    fun getFunLibraryList(@Query("category") category: String, @Query("pageNum") pageNum: Int = 0, @Query("pageSize") pageSize: Int = 15): Observable<HFFunLibraryBookResponseBody>

    /**
     * 调查问卷详情
     */
    @GET("/api/m/question/item/list")
    fun getFunQuestionDetail(@Query("infoId") infoId: Int): Observable<HFFunQuestionDetailResponseBody>

    /**
     * 提交调查问卷
     */
    @POST("/api/m/question/post")
    fun postFunQuestions(@Body aBody: HFFunQuestionRequestBody): Observable<HFBaseResponseBody>

    /**
     * 纠纷调解
     */
    @GET("/api/m/disputeResolution/list")
    fun getFunDisputeMediatorList(): Observable<HFfunDisputeMediatorResponseBody>

    /**
     * 上传文件
     */
    @Multipart
    @POST("/api/m/file/uploadFile")
    fun uploadFile(@Part file: MultipartBody.Part): Observable<HFFunUploadFileResponseBody>

    /**
     * 上传文件
     */
    fun uploadFile(file: File): Observable<HFFunUploadFileResponseBody>

    @Streaming
    @GET("{url}")
    fun downloadFile(@Path(value = "url", encoded = true) path: String): Call<ResponseBody>

    /**
     * 下载文件
     */
    fun downloadFile(path: String, directory: String, onProgress: ((progress: Int) -> Unit), onFinish: ((filePath: String) -> Unit), onFailure: ((error: Throwable) -> Unit))

    @POST("/api/m/repairReport/post")
    fun postFunLiveRepairs(@Body requestBody: HFFunLiveRepairsApplyRequestBody): Observable<HFBaseResponseBody>

}
