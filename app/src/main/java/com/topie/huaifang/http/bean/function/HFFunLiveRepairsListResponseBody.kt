package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/7.
 */
class HFFunLiveRepairsListResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var id: Int = 0
        var contactUserId: Int = 0
        var contactPerson: String? = null
        var reportTitle: String? = null
        var roomNumber: String? = null
        var contactPhone: String? = null
        var reportContent: String? = null
        var images: String? = null
        var reportTime: String? = null
        var status: String? = null
    }
}