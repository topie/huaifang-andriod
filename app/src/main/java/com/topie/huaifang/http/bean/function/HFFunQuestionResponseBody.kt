package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/9/30.
 * 调查问卷
 */
class HFFunQuestionResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var addTime: String? = null//添加时间
        var addUser: String? = null//添加人
        var name: String? = null//问卷调查名称
        var end: String? = null//结束时间
        var detail: String? = null//详情
        var id: Int = 0
        var begin: String? = null//开始时间
        var status: String? = null//状态：未发布,已发布,已回收
    }
}