package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody
import java.io.Serializable

/**
 * Created by arman on 2017/9/25.
 * 党务公开
 */
class HFFunPartyPublicResponseBody : HFBaseResponseBody() {

    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData : Serializable {
        var id: Int = 0
        var publishTime: String? = null
        var file: String? = null
        var publishUser: String? = null
        var title: String? = null
        var type: String? = null
        var content: String? = null
        var total: Int = 0//参加人数
    }
}