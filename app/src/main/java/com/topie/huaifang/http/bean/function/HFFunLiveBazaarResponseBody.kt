package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody
import java.io.Serializable

/**
 * Created by arman on 2017/10/10.
 * 社区集市
 */
class HFFunLiveBazaarResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData : Serializable {
        var id: Int = 0 //ID
        var publishTime: String? = null //发布时间
        var images: String? = null  //多个图片地址 逗号分隔
        var addTime: String? = null     //添加时间
        var addUserId: String? = null   //添加用户ID
        var headImage: String? = null   //头图地址
        var type: Int = 0   //类型 0跳蚤市场1车位共享
        var content: String? = null //内容
        var iCount: Int = 0 //阅读次数
        var contactPhone: String? = null    //联系电话
        var addUserName: String? = null //添加用户名称
        var status: Int = 0 //上线状态
    }
}