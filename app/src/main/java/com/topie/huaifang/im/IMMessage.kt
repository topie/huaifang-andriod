package com.topie.huaifang.im

import android.net.Uri

/**
 * Created by arman on 2017/10/27.
 */
class IMMessage {
    var messageId = 0
    var content: String? = null
    var userId: Int = 0
    var userName: String? = null
    var headImage: Uri? = null
    var sendTime: Long = 0.toLong()
}