package com.topie.huaifang.view

import android.content.Context
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * Created by bigniu on 2017/6/7.
 * 按比例自动填充宽高的layout，注意：必须有一边固定才能适配另一边
 */

class HFAspectRelativeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    private val measure = HFAspectRatioMeasure.create(this, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthPadding = paddingLeft + paddingRight
        val heightPadding = paddingTop + paddingBottom
        val spec = measure.updateMeasureSpec(widthMeasureSpec, heightMeasureSpec, layoutParams, widthPadding, heightPadding)
        super.onMeasure(spec.width, spec.height)
    }

    fun setAspectRatio(ratio: Float) {
        measure.mAspectRatio = ratio
    }
}
