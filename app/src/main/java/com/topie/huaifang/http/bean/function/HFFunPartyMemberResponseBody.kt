package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody
import java.io.Serializable

/**
 * Created by arman on 2017/9/25.
 * 党员信息公开
 */
class HFFunPartyMemberResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData : Serializable {
        var nodeName: String? = null    //所属党组织
        var enterDate: String? = null   //入党时间
        var code: String? = null        //党员编号
        var flag: String? = null        //党员标示
        var name: String? = null        //党员名称
        var id: Int = 0
        var tag: String? = null         //党员标签，以","分割
        var nodeId: Int = 0
    }
}