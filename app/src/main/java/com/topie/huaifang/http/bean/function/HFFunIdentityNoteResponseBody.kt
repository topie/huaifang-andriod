package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/11.
 * 身份认证
 */
class HFFunIdentityNoteResponseBody : HFBaseResponseBody() {

    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var id: Int = 0
        var pId: Int = 0
        var name: String? = null
        var roomNumber: String? = null
        var companyName: String? = null
    }
}