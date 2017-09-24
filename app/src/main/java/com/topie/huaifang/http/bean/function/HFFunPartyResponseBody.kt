package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/9/24.
 * 党建活动
 */
class HFFunPartyResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        companion object {
            const val STATUS_WAIT = "0"
            const val STATUS_GOING = "1"
            const val STATUS_FINISH = "2"
        }

        var image: String? = null
        var publishTime: String? = null
        var address: String? = null //活动地址
        var publishUser: String? = null //发布人
        var joinLimit: String? = null   //参加人数上限
        var content: String? = null //内容
        var activityRange: String? = null   //活动范围
        var topic: String? = null   //活动主题
        var beginTime: String? = null
        var endTime: String? = null
        var id: Int = 0
        var status: String? = null //0未上线1上线中2已结束
    }
}