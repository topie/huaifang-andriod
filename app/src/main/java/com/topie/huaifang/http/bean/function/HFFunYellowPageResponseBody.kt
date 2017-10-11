package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/9/29.
 * 迷你黄页
 */
class HFFunYellowPageResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var note: String? = null//备注
        var mobilePhone: String? = null//联系电话
        var name: String? = null//名称
        var id: Int = 0
        var typeStr: String? = null//类型
    }
}