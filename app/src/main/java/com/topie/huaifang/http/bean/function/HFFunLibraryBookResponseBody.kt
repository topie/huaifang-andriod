package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/2.
 * 社区图书馆
 */
class HFFunLibraryBookResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var id: Int = 0
        var content: String? = null
        var bookName: String? = null
        var author: String? = null
        var image: String? = null   //封面地址
        var status: String? = null  //已借出
    }
}