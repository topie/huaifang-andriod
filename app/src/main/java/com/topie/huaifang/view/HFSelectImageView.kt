package com.topie.huaifang.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kFindViewById

/**
 * Created by arman on 2017/10/21.
 * 选择图片的弹窗
 */
class HFSelectImageView : PopupWindow {
    constructor(context: Context) {
        val inflate = LayoutInflater.from(context).inflate(R.layout.image_select_layout, null, false)
        inflate.kFindViewById<View>(R.id.tv_image_select_camera).setOnClickListener {

        }

    }
}