package com.topie.huaifang

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

/**
 * Created by arman on 2017/7/14.
 */

interface HFService {

    @GET("homePageList?platformType=androidPhone&channelId=20&pageSize=20&requireTime=&isNotModified=0&adapterNo=7.0.0&protocol=1.0.0")
    fun getCall(): Observable<Response<Any>>
}
