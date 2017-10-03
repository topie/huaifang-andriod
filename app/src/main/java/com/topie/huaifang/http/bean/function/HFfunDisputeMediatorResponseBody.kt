package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/3.
 * 纠纷调解
 */
class HFfunDisputeMediatorResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var address: String? = null
        var contactPerson: String? = null//联系人
        var updateTime: String? = null
        var id: Int = 0
        var contactPhone: String? = null
        var title: String? = null
    }
}