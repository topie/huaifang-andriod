package com.topie.huaifang.im

/**
 * Created by arman on 2017/10/27.
 * 聊天接口
 */
interface IMInterface {

    fun sendMessage(imMessage: IMMessage, sendMessageCallback: SendMessageCallback)

    fun registerMessageReceiver(messageReceiver: MessageReceiver)

    fun unregisterMessageReceiver(messageReceiver: MessageReceiver)

    interface SendMessageCallback {
        fun callback(imMessage: IMMessage, result: Boolean)
    }

    interface MessageReceiver {
        fun onReceive(list: List<IMMessage>)
    }
}