package com.topie.huaifang

import com.topie.huaifang.converter.GsonConverterFactory
import com.topie.huaifang.extensions.log
import io.reactivex.ObservableTransformer
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
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()!!

    fun <T> compose(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe { log("doOnSubscribe") }
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}