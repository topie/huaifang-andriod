package com.topie.huaifang.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ViewFlipper
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kGetColor

/**
 *
 *
 */
class HFTextViewFlipper : ViewFlipper {

    /**
     * 动画持续时间
     */
    var animDuration = 500
        set(value) {
            inAnimation?.duration = value.toLong()
            outAnimation?.duration = value.toLong()
            field = value
        }

    init {
        inAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_marquee_in)
        outAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_marquee_out)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    /**
     * 设置数据
     *
     * @param dataList
     */
    fun setDataList2Start(dataList: List<String>?, onItemClicked: ((index: Int, s: String) -> Unit)? = null,
                          textSize: Float = 14.toFloat(), textColor: Int = kGetColor(R.color.colorGray)) {
        stopFlipping()
        removeAllViews()
        if (dataList == null || dataList.isEmpty()) {
            return
        }
        dataList.forEachIndexed { index, s ->
            val textView = TextView(context)
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            textView.layoutParams = lp
            textView.textSize = textSize
            textView.setTextColor(textColor)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.text = s
            textView.maxLines = 1
            textView.ellipsize = TextUtils.TruncateAt.END
            textView.setOnClickListener { onItemClicked?.invoke(index, s) }
            addView(textView)
        }
        if (dataList.size > 1) {
            startFlipping()
        }
    }
}
