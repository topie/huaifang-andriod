package com.topie.huaifang.imageloader

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.topie.huaifang.R

/**
 * Created by arman on 2017/9/14.
 */
class HFImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : AppCompatImageView(context, attrs, defStyleAttr) {

    var mRoundedAsCircle: Boolean = false
    var mRoundedCornerRadius: Int = 0
    var mDefImageRes: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    /**
     * 默认构造函数
     */
    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.HFImageView)
            mRoundedAsCircle = a.getBoolean(R.styleable.HFImageView_roundedAsCircle, false)
            mDefImageRes = a.getResourceId(R.styleable.HFImageView_defImageRes, 0)
            mRoundedCornerRadius = a.getDimensionPixelSize(R.styleable.HFImageView_roundedCornersRadius, 0)
            a.recycle()
        }
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
        val load = GlideApp.with(this).asBitmap().load(uri)
        loadImage(load)
    }

    private fun loadImage(load: GlideRequest<Bitmap>) {
        if (mDefImageRes != 0) {
            val bitmap: Bitmap = HFMemoryCache.get(mDefImageRes) ?: BitmapFactory.decodeResource(resources, mDefImageRes)
            HFMemoryCache.put(mDefImageRes, bitmap)
            if (mRoundedAsCircle || mRoundedCornerRadius > 0) {
                load.placeholder(HFRoundedBitmapDrawable(this, bitmap))
            }
        }
        if (mRoundedAsCircle || mRoundedCornerRadius > 0) {
            load.into(object : BitmapImageViewTarget(this) {
                override fun setResource(resource: Bitmap?) {
                    view?.setImageBitmap(resource)
                }
            })
        } else {
            load.into(this)
        }
    }
}
