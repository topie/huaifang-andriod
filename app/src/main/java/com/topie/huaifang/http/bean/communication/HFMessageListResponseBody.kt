package com.topie.huaifang.http.bean.communication

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/27.
 * 消息列表
 */
class HFMessageListResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<HFMessage>? = null
        var extra: Extra? = null
    }

    class Extra {
        var time: Long = 0.toLong()
    }
}