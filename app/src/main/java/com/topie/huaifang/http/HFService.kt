package com.topie.huaifang.http

import com.topie.huaifang.http.bean.HFAppUpdateResponseBody
import com.topie.huaifang.http.bean.HFBaseResponseBody
import com.topie.huaifang.http.bean.account.HFAccountResponseBody
import com.topie.huaifang.http.bean.account.HFAccountUserInfoRequestBody
import com.topie.huaifang.http.bean.account.HFCheckPhoneResponseBody
import com.topie.huaifang.http.bean.account.HFLoginResponseBody
import com.topie.huaifang.http.bean.communication.*
import com.topie.huaifang.http.bean.function.*
import com.topie.huaifang.http.bean.index.HFIndexNewsResponseBody
import com.topie.huaifang.http.bean.index.HFIndexTopImgResponseBody
import com.topie.huaifang.util.HFDimensUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
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
    fun getFunGuideList(@Query("catId") catId: String, @Query("pageNum") pageNum: Int = 1, @Query("pageSize") pageSize: Int = 15): Observable<HFFunGuideListResponseBody>

    /**
     * 办事指南详情
     */
    @GET("/api/m/actionGuide/detail")
    fun getFunGuideDetail(@Query("id") id: Int): Observable<HFFunGuideDetailResponseBody>

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
    fun getFunPublicDetail(@Query("id") type: Int): Observable<HFFunPublicDetailResponseBody>

    /**
     * 我的好友
     */
    @GET("/api/m/appUser/friends")
    fun getCommFriends(): Observable<HFCommFriendsResponseBody>

    /**
     * 社区党建，党建活动列表
     */
    @GET("/api/m/party/activity/list")
    fun getFunPartyActList(@Query("pageNum") pageNum: Int = 0, @Query("pageSize") pageSize: Int = 15): Observable<HFFunPartyActResponseBody>

    /**
     * 党建活动-党支部活动报名
     */
    @GET("/api/m/party/activity/join")
    fun postFunPartyAct(@Query("id") id: Int): Observable<HFBaseResponseBody>

    /**
     * 党建活动-党支部活动发布
     */
    @POST("/api/m/party/activity/post")
    @FormUrlEncoded
    fun postFunPartyActPublish(@FieldMap requestBody: Map<String, String>): Observable<HFBaseResponseBody>

    /**
     * 党建活动-党支部活动详情
     */
    @GET("/api/m/party/activity/detail")
    fun getFunPartyActDetail(@Query("id") id: Int): Observable<HFFunPartyActDetailResponseBody>

    /**
     * 党务公开
     */
    @GET("/api/m/party/business/list")
    fun getFunPartyPublicList(): Observable<HFFunPartyPublicResponseBody>

    /**
     * 党务公开
     */
    @GET("/api/m/party/business/detail")
    fun getFunPartyPublicDetail(@Query("id") id: Int): Observable<HFFunPartyPublicDetailResponseBody>

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

    /**
     * 上传图片
     */
    fun uploadImage(file: File, targetWidth: Int = HFDimensUtils.screenWidth / 2, targetHeight: Int = HFDimensUtils.screenHeight / 2): Observable<HFFunUploadFileResponseBody>

    @Streaming
    @GET("{url}")
    fun downloadFile(@Path(value = "url", encoded = true) path: String): Call<ResponseBody>

    /**
     * 下载文件
     */
    fun downloadFile(path: String, directory: String, onProgress: ((progress: Int) -> Unit), onFinish: ((filePath: String) -> Unit), onFailure: ((error: Throwable) -> Unit)): Disposable

    @POST("/api/m/repairReport/post")
    fun postFunLiveRepairs(@Body requestBody: HFFunLiveRepairsApplyRequestBody): Observable<HFBaseResponseBody>

    /**
     * 物业报修
     */
    @GET("/api/m/repairReport/list")
    fun getFunLiveRepairsList(): Observable<HFFunLiveRepairsListResponseBody>

    /**
     * 物业保修进度
     */
    @GET("/api/m/repairReport/process/list")
    fun getFunLiveRepairsProgress(@Query("id") id: Int): Observable<HFFunLiveRepairsProgressResponseBody>

    @POST("/api/m/marketLine/post")
    fun postFunLiveBazaar(@Body requestBody: HFFunLiveBazaarApplyRequestBody): Observable<HFBaseResponseBody>

    @GET("/api/m/marketLine/list")
    fun getFunLiveBazaarList(@Query("type") type: Int, @Query("pageNum") pageNum: Int = 0, @Query("pageSize") pageSize: Int = 15): Observable<HFFunLiveBazaarResponseBody>

    /**
     * 发布邻里圈
     */
    @POST("/api/m/appTimeLine/post")
    fun postFunDisNeighborhood(@Body requestBody: HFFunLiveBazaarApplyRequestBody): Observable<HFBaseResponseBody>

    @GET("/api/m/appTimeLine/list")
    fun getFunDisNeighborhoodList(@Query("pageNum") pageNum: Int = 1, @Query("pageSize") pageSize: Int = 15): Observable<HFFunDisNeighborhoodResponseBody>

    @GET("/api/m/appTimeLine/like")
    fun postFunDisLike(@Query("id") id: Int): Observable<HFBaseResponseBody>

    @GET("/api/m/appTimeLine/unlike")
    fun postFunDisUnlike(@Query("id") id: Int): Observable<HFBaseResponseBody>

    @POST("/api/m/appTimeLine/comment")
    fun postFunDisComment(@Body requestBody: HFFunDisNeighCommRequestBody): Observable<HFBaseResponseBody>

    /**
     * 发布活动
     */
    @POST("/api/m/aroundActivity/post")
    @FormUrlEncoded
    fun postFunDisAction(@FieldMap map: Map<String, String>): Observable<HFBaseResponseBody>

    /**
     * 活动列表
     */
    @GET("/api/m/aroundActivity/list")
    fun getFunDisActionList(@Query("type") type: Int,@Query("pageNum") pageNum: Int = 0, @Query("pageSize") pageSize: Int = 15): Observable<HFFunDisActionListResponseBody>


    /**
     * 活动详情
     */
    @GET("/api/m/aroundActivity/detail")
    fun getFunDisActionDetail(@Query("id") id: Int): Observable<HFFunDisActionDetailResponseBody>

    /**
     * 活动报名
     */
    @GET("/api/m/aroundActivity/join")
    fun postFunDisAction(@Query("id") id: Int): Observable<HFBaseResponseBody>

    /**
     * 我的信息
     */
    @GET("/api/m/appUser/authInfo")
    fun getFunIdentityDate(): Observable<HFFunIdentityResponseBody>

    /**
     * 房间联动信息
     */
    @GET("/api/m/houseInfo/node")
    fun getFunIdentityNote(@Query("parentId") parentId: Int = 0): Observable<HFFunIdentityNoteResponseBody>

    /**
     * 房间联动信息
     */
    @GET("/api/m/houseInfo/house")
    fun getFunIdentityRoom(@Query("nodeId") nodeId: Int): Observable<HFFunIdentityNoteResponseBody>

    /**
     * 企业信息
     */
    @GET("/api/m/companyInfo/list")
    fun getFunIdentityCompany(): Observable<HFFunIdentityNoteResponseBody>

    /**
     * 发布信息认证
     */
    @POST("/api/m/appUser/auth")
    fun postFunIdentity(@Body requestBody: HFFunIdentityEditRequestBody): Observable<HFBaseResponseBody>

    /**
     * 首页消息列表
     */
    @GET("/api/m/index/news")
    fun getIndexNews(): Observable<HFIndexNewsResponseBody>

    /**
     * 首页头图
     */
    @GET("/api/m/index/head")
    fun getIndexTopImage(): Observable<HFIndexTopImgResponseBody>

    /**
     * 账户信息
     */
    @GET("/api/m/index/userInfo")
    fun getAccountInfo(): Observable<HFAccountResponseBody>

    @POST("/api/m/appUser/updateInfo")
    fun postAccountInfo(@Body hfAccountUserInfoRequestBody: HFAccountUserInfoRequestBody): Observable<HFBaseResponseBody>

    /**
     * 发表意见
     */
    @POST("/api/m/adviceBox/post")
    fun postFunAdvice(@Body hfFunAdviceRequestBody: HFFunAdviceRequestBody): Observable<HFBaseResponseBody>

    /**
     * 意见列表
     */
    @GET("/api/m/adviceBox/list")
    fun getFunAdviceList(): Observable<HFFunAdviceResponseBody>

    /**
     * 发送消息
     */
    @POST("/api/m/appUserMessage/send")
    fun postMessage(@Body hfMessageRequestBody: HFMessageRequestBody): Observable<HFMessageResponseBody>

    /**
     * 消息列表
     */
    @GET("/api/m/appUserMessage/list")
    fun getMessageList(@Query("fromUserId") fromUserId: Int, @Query("time") time: Long): Observable<HFMessageListResponseBody>


    /**
     * 消息列表
     */
    @GET("/api/m/appManager/current?system=android")
    fun getAppUpdate(): Observable<HFAppUpdateResponseBody>
}
