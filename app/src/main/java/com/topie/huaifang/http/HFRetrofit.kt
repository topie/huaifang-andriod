package com.topie.huaifang.http

import android.util.Log
import com.topie.huaifang.BuildConfig
import com.topie.huaifang.http.converter.GsonConverterFactory
import com.topie.huaifang.extensions.log
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
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
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()!!

    val hfService: HFService = retrofit.create(HFService::class.java)
}

fun <T> Observable<T>.composeApi(): Observable<T> {
    subscribeOn(Schedulers.io())
    observeOn(AndroidSchedulers.mainThread())
    return this
}

private val consumer = Consumer<Throwable> {
    if (BuildConfig.DEBUG) {
        throw it
    } else {
        Log.e("consumer", "", it)
    }
}

fun <T> Observable<T>.subscribeApi(onNext: Consumer<T>) {
    subscribe(onNext, consumer)
}

fun <T> Observable<T>.subscribeApi(onNext: Consumer<T>, onError: Consumer<Throwable>) {
    subscribe(onNext, onError)
}