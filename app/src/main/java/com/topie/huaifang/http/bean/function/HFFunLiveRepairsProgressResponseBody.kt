package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/10.
 * 保修进度
 */
class HFFunLiveRepairsProgressResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var id: Int = 0
        var reportId: Int = 0
        var processStatus: String? = null   //进度情况
        var contactUserId: Int = 0  //联系人ID
        var contactPerson: String? = null   //联系人
        var contactPhone: String? = null    //联系手机
        var processContent: String? = null  //进度情况
        var processTime: String? = null //进度时间
        var status: String? = null
    }
}
