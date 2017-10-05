package com.topie.huaifang.http

import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.bean.HFBaseResponseBody
import com.topie.huaifang.http.bean.function.HFFunUploadFileResponseBody
import com.topie.huaifang.http.converter.HFGsonConverterFactory
import com.topie.huaifang.util.HFLogger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File


/**
 * Created by arman on 2017/7/14.
 */
object HFRetrofit {

    const val baseUrl = "http://huaifang.zt647.com/"

    private var client: OkHttpClient

    private var retrofit: Retrofit

    var hfService: HFService
        private set

    init {
        client = OkHttpClient.Builder().build()!!
        retrofit = buildRetrofit()
        hfService = HFServiceDerived(retrofit.create(HFService::class.java))
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
        hfService = HFServiceDerived(retrofit.create(HFService::class.java))
    }
}

private class HFServiceDerived(service: HFService) : HFService by service {
    override fun uploadFile(file: File): Observable<HFFunUploadFileResponseBody> {
        val requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        RequestBody.create(MediaType.parse("application/otcet-stream"), file)
        return uploadFile(body)
    }
}

fun <T> Observable<T>.composeApi(): Observable<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T : HFBaseResponseBody> Observable<T>.subscribeResultOkApi(onNext: ((t: T) -> Unit)): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        when {
            it.resultOk -> onNext(it)
            else -> it.convertMessage().kToastShort()
        }
    }, {
        HFLogger.log("error", it)
        it.message?.kToastShort()
    })
}

fun <T : HFBaseResponseBody> Observable<T>.subscribeApi(onNext: ((t: T) -> Unit)): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, {
        HFLogger.log("error", it)
        it.message?.kToastShort()
    })
}

fun <T : HFBaseResponseBody> Observable<T>.subscribeResultOkApi(onNext: ((t: T) -> Unit), onComplete: () -> Unit): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        when {
            it.resultOk -> onNext(it)
            else -> it.convertMessage().kToastShort()
        }
    }, {
        HFLogger.log("error", it)
        it.message?.kToastShort()
    }, onComplete)
}

fun <T : HFBaseResponseBody> Observable<T>.subscribeApi(onNext: ((t: T) -> Unit), onComplete: () -> Unit): Disposable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, {
        HFLogger.log("error", it)
        it.message?.kToastShort()
    }, onComplete)
}