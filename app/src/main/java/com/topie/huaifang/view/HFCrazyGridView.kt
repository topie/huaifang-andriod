package com.topie.huaifang.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridView

class HFCrazyGridView(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = -1) : GridView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }

}