package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/11.
 * 邻里圈
 */
class HFFunDisNeighborhoodResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<ListData>? = null
    }

    class ListData {
        var id: Int = 0
        var addUserName: String? = null
        var publishTime: String? = null
        var images: String? = null
        var addUserId: Int = 0
        var headImage: String? = null
        var content: String? = null
        var comments: MutableList<CommData>? = null
        var likes: MutableList<LikeData>? = null
    }

    class LikeData {
        var userId: Int = 0
        var userName: String? = null
        var likeTime: String? = null
    }

    class CommData {
        var reCommentId: String? = null
        var userName: String? = null
        var commentTime: String? = null
        var content: String? = null
    }
}