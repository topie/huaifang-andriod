package com.topie.huaifang.http

import com.topie.huaifang.http.converter.HFGsonConverterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * Created by arman on 2017/7/14.
 */
object HFRetrofit {

    private val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()!!

    val retrofit = Retrofit.Builder()
            .baseUrl("http://huaifang.zt647.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(HFGsonConverterFactory.create())
            .client(client)
            .build()!!

    val hfService: HFService = retrofit.create(HFService::class.java)
}

fun <T> Observable<T>.composeApi(): Observable<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}