package com.topie.huaifang.im

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.extensions.kSimpleFormatToDate
import com.topie.huaifang.extensions.log
import com.topie.huaifang.global.IMConstant
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.communication.HFMessage
import com.topie.huaifang.http.bean.communication.HFMessageRequestBody
import com.topie.huaifang.http.composeApi
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by arman on 2017/10/27.
 * 轮询api的消息实现
 */
class IMApiLooperService(val connectUserId: Int) : IMInterface {

    private var lastRequestTime: Long = 0.toLong()
    private var mMessageReceiver: IMInterface.MessageReceiver? = null

    private val list: MutableList<IMMessage> = mutableListOf()
    private var mDisposable: Disposable? = null

    companion object {
        const val WHAT_SUCCESS = 100
        const val WHAT_FAILURE = -100

    }

    private fun convert2IMMessage(it: HFMessage, imMessage: IMMessage = IMMessage()): IMMessage {
        imMessage.messageId = it.id
        imMessage.content = it.content
        imMessage.userId = it.fromUserId
        imMessage.sendTime = it.sendTime?.kSimpleFormatToDate()?.time?.let { it / 1000 } ?: 0.toLong()
        imMessage.headImage = it.headImage.kParseUrl()
        imMessage.userName = it.fromUserName
        return imMessage
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                WHAT_SUCCESS -> {
                    val list = msg.obj as List<IMMessage>
                    mMessageReceiver?.onReceive(list)
                }
            }
        }
    }

    override fun sendMessage(imMessage: IMMessage, sendMessageCallback: IMInterface.SendMessageCallback) {
        list.add(imMessage)
        val hfMessageRequestBody = HFMessageRequestBody()
        hfMessageRequestBody.content = imMessage.content
        hfMessageRequestBody.toUserId = connectUserId
        HFRetrofit.hfService.postMessage(hfMessageRequestBody).composeApi().subscribe({
            if (it.resultOk && it.data != null) {
                convert2IMMessage(it.data!!, imMessage)
                sendMessageCallback.callback(imMessage, true)
            } else {
                sendMessageCallback.callback(imMessage, false)
            }
        }, {
            sendMessageCallback.callback(imMessage, false)
        })
    }

    override fun registerMessageReceiver(messageReceiver: IMInterface.MessageReceiver) {
        mMessageReceiver = messageReceiver
    }

    override fun unregisterMessageReceiver(messageReceiver: IMInterface.MessageReceiver) {
        stopLooper()
        mMessageReceiver = null
    }

    override fun resume() {
        getMessage()
    }

    override fun pause() {
        mDisposable?.takeIf { !it.isDisposed }?.dispose()
    }

    private fun stopLooper() {
        mDisposable?.takeIf { !it.isDisposed }?.dispose()
    }

    private fun getMessage() {
        mDisposable?.takeIf { !it.isDisposed }?.dispose()
        Observable.interval(0, IMConstant.COMMUNICATION_MSG_PERIOD, TimeUnit.MILLISECONDS).flatMap {
            log("getMsgList[$it]")
            HFRetrofit.hfService.getMessageList(connectUserId, lastRequestTime)
        }.subscribeOn(Schedulers.io()).subscribe({
            if (it.resultOk && it.data?.extra?.time != null) {
                lastRequestTime = it.data!!.extra!!.time
                val obtainMessage = handler.obtainMessage(WHAT_SUCCESS)
                obtainMessage.obj = it.data?.data
                        ?.takeIf { it.isNotEmpty() }
                        ?.map { convert2IMMessage(it) }
                        ?.filter { msg ->
                            when {
                                list.isEmpty() -> true //如果发送集合是空的，不筛选任何数据
                                else -> list.firstOrNull { it.messageId == msg.messageId } == null //如果发送集合不是空，筛选（排除）刚刚发送的信息
                            }
                        }
                        ?.takeIf { it.isNotEmpty() } //如果集合是空的，做空值处理
                        ?: return@subscribe
                obtainMessage.sendToTarget()
            }
        }, {}).also { mDisposable = it }
    }

}