package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/9/21.
 * 办事指南
 */
class HFFunGuideMenuResponseBody : HFBaseResponseBody() {

    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {

        /**
         *  ...
         */
        var note: String? = null
        /**
         *  地址
         */
        var address: String? = null
        /**
         *  联系电话
         */
        var phone: String? = null
        /**
         *  导航名称
         */
        var name: String? = null
        /**
         * 联系人
         */
        var contactPerson: String? = null
        /**
         *  导航id
         */
        var id: String? = null
        /**
         *  简介
         */
        var title: String? = null
        /**
         *  ...
         */
        var status: String? = null
    }
}