package com.topie.huaifang.view

import android.content.Context
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.widget.LinearLayout
import com.topie.huaifang.R

/**
 * Created by bigniu on 2017/6/7.
 * 按比例自动填充宽高的layout，注意：必须有一边固定才能适配另一边
 */

class HFAspectLinearLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private val measure = HFAspectRatioMeasure.create(this, attrs)
    private val aspectHorizontalPadding: Int
    private val aspectVerticalPadding: Int

    init {
        val typedArray = when (attrs) {
            null -> null
            else -> context.obtainStyledAttributes(attrs, R.styleable.HFAspectLinearLayout)
        }
        if (typedArray == null) {
            aspectHorizontalPadding = 0
            aspectVerticalPadding = 0
        } else {
            aspectHorizontalPadding = typedArray.getDimensionPixelSize(R.styleable.HFAspectLinearLayout_aspectHorizontalPadding, 0)
            aspectVerticalPadding = typedArray.getDimensionPixelSize(R.styleable.HFAspectLinearLayout_aspectVerticalPadding, 0)
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthPadding = paddingLeft + paddingRight + aspectHorizontalPadding
        val heightPadding = paddingTop + paddingBottom + aspectVerticalPadding
        val spec = measure.updateMeasureSpec(widthMeasureSpec, heightMeasureSpec, layoutParams, widthPadding, heightPadding)
        super.onMeasure(spec.width, spec.height)
    }

    fun setAspectRatio(ratio: Float) {
        measure.mAspectRatio = ratio
    }
}
