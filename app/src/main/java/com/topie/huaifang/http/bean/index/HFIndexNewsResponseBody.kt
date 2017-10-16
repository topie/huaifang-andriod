package com.topie.huaifang.http.bean.index

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/16.
 * 首页消息列表
 */
class HFIndexNewsResponseBody : HFBaseResponseBody() {

    var data: List<BodyData>? = null

    class BodyData {
        companion object {
            /**
             * 调查问卷
             */
            const val TYPE_QUESTION = 1
            /**
             * 社区公告
             */
            const val TYPE_LIVE_NOTICE = 2
            /**
             * 物业公告
             */
            const val TYPE_WU_YE_NOTICE = 3
        }

        var type: Int = 0
        var list: List<ListData>? = null
    }

    class ListData {
        var id: Int = 0
        var title: String? = null
    }
}