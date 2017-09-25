package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/9/22.
 */
class HFLiveListResponseBody : HFBaseResponseBody() {

    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var title: String? = null
        var id: String? = null
        var updateTime: String? = null
        var content: String? = null
    }
}