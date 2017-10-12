package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/9/21.
 */
class HFFunGuideListResponseBody : HFBaseResponseBody() {

    var data: BodyData? = null

    class BodyData {
        var total: String? = null
        var data: List<HFFunGuideListResponseBody.ListData>? = null
    }

    class ListData {
        var actionFee: String? = null           //办事费用
        var image: String? = null               //封面
        var publishTime: String? = null         //发布时间
        var publishUser: String? = null         //发布者
        var actionAddress: String? = null       //办事地址
        var readCount: String? = null           //阅读次数
        var title: String? = null               //办事指南标题
        var catId: String? = null               //所属导航
        var actionCondition: String? = null     //办事条件
        var actionYiju: String? = null          //办事依据
        var actionMaterial: String? = null      //办事材料
        var file: String? = null                //附件地址
        var actionEnd: String? = null           //结束时间
        var actionBegin: String? = null         //开始时间
        var actionNote: String? = null          //办事备注
        var id: Int = 0                  //ID
        var actionFlow: String? = null          //流程
        var allContent: String? = null          //所有内容
    }
}