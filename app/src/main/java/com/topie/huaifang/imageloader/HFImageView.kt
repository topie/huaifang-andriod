package com.topie.huaifang.imageloader

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.widget.ImageView
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.topie.huaifang.R
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.view.HFAspectRatioMeasure

/**
 * Created by arman on 2017/9/14.
 */
open class HFImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr) {

    var mRoundedAsCircle: Boolean = false
    var mRoundedCornerRadius: Int = 0
    var mDefImageRes: Int = 0
    var mBorderWidth: Int = 0
    var mBorderColor: Int = 0

    private val measure = HFAspectRatioMeasure.create(this, attrs)

    /**
     * 默认构造函数
     */
    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.HFImageView)
            mRoundedAsCircle = a.getBoolean(R.styleable.HFImageView_roundedAsCircle, false)
            mDefImageRes = a.getResourceId(R.styleable.HFImageView_defImageRes, 0)
            mRoundedCornerRadius = a.getDimensionPixelSize(R.styleable.HFImageView_roundedCornersRadius, 0)
            mBorderWidth = a.getDimensionPixelSize(R.styleable.HFImageView_borderWidth, 0)
            mBorderColor = a.getColor(R.styleable.HFImageView_borderColor, Color.TRANSPARENT)
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthPadding = paddingLeft + paddingRight
        val heightPadding = paddingTop + paddingBottom
        val spec = measure.updateMeasureSpec(widthMeasureSpec, heightMeasureSpec, layoutParams, widthPadding, heightPadding)
        super.onMeasure(spec.width, spec.height)
    }

    fun setAspectRatio(ratio: Float) {
        measure.mAspectRatio = ratio
    }

    fun loadImageUri(uri: Uri?, activity: Activity) {
        val load = GlideApp.with(activity).asBitmap().load(uri)
        loadImage(load)
    }

    fun loadImageUri(uri: Uri?, fragment: Fragment) {
        val load = GlideApp.with(fragment).asBitmap().load(uri)
        loadImage(load)
    }

    fun loadImageUri(uri: Uri?) {
        val uri1 = uri?.toString()?.takeIf {
            it.length > 1 && it[0] == '/'
        }?.let {
            Uri.parse(HFRetrofit.baseUrl + it.substring(1))
        } ?: uri
        val load = GlideApp.with(this).asBitmap().load(uri1)
        loadImage(load)
    }

    private fun loadImage(load: GlideRequest<Bitmap>) {
        if (mDefImageRes != 0) {
            val bitmap: Bitmap = HFMemoryCache.get(mDefImageRes)?.takeIf {
                !it.isRecycled
            } ?: BitmapFactory.decodeResource(resources, mDefImageRes).also {
                HFMemoryCache.put(mDefImageRes, it)
            }
            val drawable = HFRoundedBitmapDrawable(this, bitmap)
            drawable.isCircle = mRoundedAsCircle
            drawable.setRadius(mRoundedCornerRadius.toFloat())
            drawable.setBorder(mBorderColor, mBorderWidth.toFloat())
            load.placeholder(drawable)
        }
        load.into(object : BitmapImageViewTarget(this) {
            override fun setResource(bitmap: Bitmap?) {
                bitmap ?: return
                view ?: return
                val drawable = HFRoundedBitmapDrawable(view, bitmap)
                drawable.isCircle = mRoundedAsCircle
                drawable.setRadius(mRoundedCornerRadius.toFloat())
                drawable.setBorder(mBorderColor, mBorderWidth.toFloat())
                view.setImageDrawable(drawable)
            }
        })
    }
}
