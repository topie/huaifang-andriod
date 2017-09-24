package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/9/22.
 */
class HFLiveMenuResponseBody : HFBaseResponseBody() {
    var data: List<BodyData>? = null

    class BodyData {
        var title: String? = null
        var id: String? = null
        var updateTime: String? = null
        var content: String? = null
    }
}