package com.topie.huaifang.http

import com.topie.huaifang.extensions.HFContext
import com.topie.huaifang.extensions.kToastLong
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.bean.HFBaseResponseBody
import com.topie.huaifang.http.converter.HFGsonConverterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * Created by arman on 2017/7/14.
 */
object HFRetrofit {

    private const val baseUrl = "http://huaifang.zt647.com/"

    private var client: OkHttpClient
        private set

    private var retrofit: Retrofit

    var hfService: HFService
        private set

    init {
        client = OkHttpClient.Builder().build()!!
        retrofit = buildRetrofit()
        hfService = retrofit.create(HFService::class.java)
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(HFGsonConverterFactory.create())
                .client(client)
                .build()!!
    }

    /**
     * 添加注射器
     */
    fun addIntercept(interceptor: Interceptor) {
        client = client.newBuilder().addInterceptor(interceptor).build()
        retrofit = buildRetrofit()
        hfService = retrofit.create(HFService::class.java)
    }
}

fun <T> Observable<T>.composeApi(): Observable<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T : HFBaseResponseBody> Observable<T>.subscribeApi(onNext: ((t: T) -> Unit)): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, {
        "error".kToastShort()
    })
}

fun <T : HFBaseResponseBody> Observable<T>.subscribeApi(onNext: ((t: T) -> Unit), onComplete: () -> Unit): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        when {
            it.resultOk -> onNext(it)
            else -> it.convertMessage().kToastShort()
        }
    }, {
        "error".kToastShort()
    }, onComplete)
}