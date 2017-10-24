package com.topie.huaifang.http.bean.function

/**
 * Created by arman on 2017/10/22.
 * 社区党建--党支部活动发布
 */
class HFFunDisActionPublishRequestBody {
    var topic: String? = null   //活动主题
    var type: String? = null//活动类型
    var beginTime: String? = null //开始时间
    var endTime: String? = null   //结束时间
    var applyEndTime: String? = null   //结束时间
    var limit: Int = 0
    var address: String? = null //活动地址
    var content: String? = null //内容
    var image: String? = null //图片

    fun toMap(): Map<String, String> {
        val map = hashMapOf<String, String>()
        map.put("title", topic ?: "")
        map.put("image", image ?: "")
        map.put("type", type ?: "")
        map.put("address", address ?: "")
        map.put("joinLimit", limit.toString())
        map.put("content", content ?: "")
        map.put("beginTime", beginTime ?: "")
        map.put("endTime", endTime ?: "")
        return map
    }
}