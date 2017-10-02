package com.topie.huaifang.http.bean.function

import com.google.gson.annotations.SerializedName
import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/2.
 * 问卷调查详情
 */
class HFFunQuestionDetailResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var questionIndex: Int = 0
        var infoId: Int = 0
        @SerializedName("id")
        var questionId: Int = 0
        var question: String? = null
        var options: List<Option>? = null
    }

    class Option {
        var optionId: Int = 0
        var optionText: String? = null
    }
}