package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody
import java.io.Serializable

/**
 * Created by arman on 2017/10/24.
 * 发现菜单
 */
class HFFunDisActionListResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData : Serializable {
        var image: String? = null
        var type: String? = null
        var publishTime: String? = null
        var address: String? = null //活动地址
        var publishUser: String? = null //发布人
        var joinLimit: Int = 0   //参加人数上限
        var content: String? = null //内容
        var activityRange: String? = null   //活动范围
        var title: String? = null   //活动主题
        var beginTime: String? = null
        var endTime: String? = null
        var id: Int = 0
        var status: String? = null //0未上线1上线中2已结束
        var total: Int = 0//以参加人数
        var hasJoin: Boolean = false//是否已经报名
    }
}