package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/9/25.
 * 党务公开
 */
class HFFunPartyPublicResponseBody : HFBaseResponseBody() {

    var data: List<BodyData>? = null

    class BodyData {
        var id: Int = 0
        var publishTime: String? = null
        var file: String? = null
        var publishUser: String? = null
        var title: String? = null
        var type: String? = null
        var content: String? = null
    }
}