package com.topie.huaifang.http.bean.communication

/**
 * Created by arman on 2017/9/28.
 */
class HFCommMsgDetail {
    var createTime: String? = null
    var fromUserId: Int = 0
    var fromUserName: String? = null
    var eventTime: String? = null//消息时间
    var icon: String? = null
    var isRead: Int = 0
    var id: Int = 0
    var category: Int = 0
    var title: String? = null
    var type: Int = 0       //类型0系统消息1好友消息
    var toUserId: Int = 0
    var content: String? = null
}