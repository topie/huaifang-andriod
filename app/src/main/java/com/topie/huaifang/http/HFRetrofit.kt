package com.topie.huaifang.http

import android.net.Uri
import android.os.AsyncTask
import com.topie.huaifang.extensions.*
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
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


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

    fun parseUrlToBase(url: String): Uri {
        return url.takeIf {
            it.length > 1 && it[0] == '/'
        }?.let {
            Uri.parse(HFRetrofit.baseUrl + it.substring(1))
        } ?: Uri.parse(url)
    }
}

private class HFServiceDerived(service: HFService) : HFService by service {

    override fun uploadFile(file: File): Observable<HFFunUploadFileResponseBody> {
        val requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        RequestBody.create(MediaType.parse("application/otcet-stream"), file)
        return uploadFile(body)
    }

    override fun uploadImage(file: File, targetWidth: Int, targetHeight: Int): Observable<HFFunUploadFileResponseBody> {
        return Observable.create<File?> {
            val compressFile = kGetExtraCacheDir()?.let {
                File(it, Date().kToSimpleFormat() + ".jpg")
            }?.takeIf {
                kCompress(file, it, targetWidth, targetHeight)
            }
            it.onNext(compressFile)
            it.onComplete()
        }.subscribeOn(Schedulers.io()).flatMap {
            if (it != null) {
                uploadFile(it)
            } else {
                Observable.create {
                    throw IOException("图片上传失败")
                }
            }
        }
    }

    override fun downloadFile(path: String, directory: String, onProgress: (progress: Int) -> Unit, onFinish: (filePath: String) -> Unit, onFailure: (error: Throwable) -> Unit) {

        class Task : AsyncTask<String, Int, Any>() {
            override fun doInBackground(vararg params: String?): Any {
                val response = downloadFile(path).execute()
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    val body = response.body()
                    val subtype = body?.contentType()?.subtype()?.let { ".$it" } ?: ""
                    inputStream = body?.byteStream()
                    val fileName = SimpleDateFormat("yyyyMMdd-HHmmssSSS", Locale.getDefault()).format(Date()) + subtype
                    val file = File(directory).let { File(it, fileName) }
                    outputStream = file.let { FileOutputStream(it, false) }

                    if (inputStream == null) {
                        return IOException("inputStream is null")
                    }
                    val fileReader = ByteArray(4096)
                    val fileSize = body?.contentLength() ?: 0.toLong()
                    var fileDownload = 0.toLong()
                    while (true) {
                        val read = inputStream.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        outputStream.write(fileReader, 0, read)
                        fileDownload += read
                        if (fileDownload == 0.toLong() || fileSize == 0.toLong()) {
                            publishProgress(0)
                        } else {
                            publishProgress((fileDownload / fileSize).toInt())
                        }
                        outputStream.flush()
                    }
                    return file
                } catch (e: Throwable) {
                    return e
                } finally {
                    inputStream.kSafeClose()
                    outputStream?.kSafeClose()
                }
            }

            override fun onProgressUpdate(vararg values: Int?) {
                super.onProgressUpdate(*values)
                onProgress.invoke(values[0] ?: 0)
            }

            override fun onPostExecute(result: Any?) {
                super.onPostExecute(result)
                when (result) {
                    is Throwable -> {
                        onFailure(result)
                        log("download failure", result)
                    }
                    is File -> {
                        onFinish(result.absolutePath)
                        log("download successful, path = ${result.absolutePath}")
                    }
                    else -> {
                        onFailure(Exception("unknown result{$result}"))
                        log("download failure, unknown result = $result")
                    }
                }
            }
        }
        Task().execute()
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