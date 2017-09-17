/*
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.topie.huaifang.view

import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.topie.huaifang.R

/**
 * A utility class that performs measuring based on the desired aspect ratio.
 */
internal class HFAspectRatioMeasure(private val view: View, var mAspectRatio: Float) {

    private val spec: Spec = Spec()

    companion object {
        fun create(view: View, attrs: AttributeSet? = null): HFAspectRatioMeasure {
            val ratio: Float = attrs?.let {
                val typedArray = view.context.obtainStyledAttributes(attrs, R.styleable.HFAspectRatio)
                val ratio = typedArray.getFloat(R.styleable.HFAspectRatio_aspectRatio, 0.toFloat())
                typedArray.recycle()
                return@let ratio
            } ?: 0.toFloat()
            return HFAspectRatioMeasure(view, ratio)
        }
    }

    /**
     * Holder for width and height measure specs.
     */
    class Spec {
        var width: Int = 0
        var height: Int = 0
    }

    /**
     * Updates the given measure spec with respect to the aspect ratio.
     *
     * Note: Measure spec is not changed if the aspect ratio is not greater than zero or if
     * layoutParams is null.
     *
     * Measure spec of the layout dimension (width or height) specified as "0dp" is updated
     * to match the measure spec of the other dimension adjusted by the aspect ratio. Exactly one
     * layout dimension should be specified as "0dp".
     *
     * Padding is taken into account so that the aspect ratio refers to the content without
     * padding: `aspectRatio == (viewWidth - widthPadding) / (viewHeight - heightPadding)`
     *
     * Updated measure spec respects the parent's constraints. I.e. measure spec is not changed
     * if the parent has specified mode `EXACTLY`, and it doesn't exceed measure size if parent
     * has specified mode `AT_MOST`.
     */
    fun updateMeasureSpec(widthMeasureSpec: Int, heightMeasureSpec: Int): Spec {
        spec.width = widthMeasureSpec
        spec.height = heightMeasureSpec
        val widthPadding = view.paddingLeft + view.paddingRight
        val heightPadding = view.paddingTop + view.paddingBottom
        val aspectRatio = mAspectRatio
        val width = view.layoutParams.width
        if (shouldAdjust(view.layoutParams.height)) {
            val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val desiredHeight = ((widthSpecSize - widthPadding) / aspectRatio + heightPadding).toInt()
            val resolvedHeight = View.resolveSize(desiredHeight, heightMeasureSpec)
            spec.height = View.MeasureSpec.makeMeasureSpec(resolvedHeight, View.MeasureSpec.EXACTLY)
        } else if (shouldAdjust(width)) {
            val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)
            val desiredWidth = ((heightSpecSize - heightPadding) * aspectRatio + widthPadding).toInt()
            val resolvedWidth = View.resolveSize(desiredWidth, widthMeasureSpec)
            spec.width = View.MeasureSpec.makeMeasureSpec(resolvedWidth, View.MeasureSpec.EXACTLY)
        }
        return spec
    }

    private fun shouldAdjust(layoutDimension: Int): Boolean {
        // Note: wrap_content is supported for backwards compatibility, but should not be used.
        return layoutDimension == 0 || layoutDimension == ViewGroup.LayoutParams.WRAP_CONTENT
    }
}