package com.topie.huaifang.http.bean.function

/**
 * Created by arman on 2017/10/22.
 * 社区党建--党支部活动发布
 */
class HFFunPartyActPublishRequestBody {
    var topic: String? = null   //活动主题
    var address: String? = null //活动地址
    var beginTime: String? = null //开始时间
    var endTime: String? = null   //结束时间
    var content: String? = null //内容
    var image: String? = null //图片
    var publishUser: String? = null //发布人
}