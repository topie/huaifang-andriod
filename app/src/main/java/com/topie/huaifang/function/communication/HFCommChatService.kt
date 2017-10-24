package com.topie.huaifang.function.communication

/**
 * Created by arman on 2017/10/24.
 * 聊天服务
 */
class HFCommChatService private constructor(private val toUserId: Int) {

    companion object {
        fun createService(toUserId: Int): HFCommChatService {
            return HFCommChatService(toUserId)
        }
    }

    private fun requestMsgList(toUserId: Int, lastTime: Long) {

    }

    private fun sendMsg(toUserId: Int, msg: String) {

    }

    interface OnReceiveMsgListener {

    }

}