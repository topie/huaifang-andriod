package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/26.
 * 意见箱
 */
class HFFunAdviceResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var messageTime: String? = null     //意见留言时间
        var contactUserId: String? = null   //意见留言人ID
        var contactPerson: String? = null   //意见留言人名称
        var contactPhone: String? = null
        var messageContent: String? = null  //意见内容
        var contactEmail: String? = null
        var handleType: String? = null
        var handleDesc: String? = null
        var handlePerson: String? = null    //回复人名称
        var status: String? = null
    }
}