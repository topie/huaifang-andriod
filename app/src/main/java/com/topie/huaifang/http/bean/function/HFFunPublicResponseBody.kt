package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/9/22.
 */
class HFFunPublicResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: String? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var cTime: String? = null
        var isOnline: Boolean = false
        var bannerUri: String? = null
        var title: String? = null
        var type: Int = 0
        var content: String? = null
        var pTime: String? = null
        var cUser: String? = null
        var id: String? = null
        var position: Int = 0
    }
}