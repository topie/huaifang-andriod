package com.topie.huaifang.imageloader

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView

import java.lang.ref.WeakReference
import java.util.Arrays

/**
 * A drawable that can have rounded corners.
 */
@Suppress("KDocUnresolvedReference")
class HFRoundedBitmapDrawable(res: Resources, bitmap: Bitmap) : BitmapDrawable(res, bitmap) {

    /**
     * Returns whether or not this drawable rounds as circle.
     */
    /**
     * Sets whether to round as circle.
     *
     * @param isCircle whether or not to round as circle
     */
    var isCircle = false
        set(isCircle) {
            field = isCircle
            mIsPathDirty = true
            invalidateSelf()
        }
    private var mRadiiNonZero = false
    /**
     * Gets the radii.
     */
    /**
     * Specify radii for each of the 4 corners. For each corner, the array
     * contains 2 values, [X_radius, Y_radius]. The corners are ordered
     * top-left, top-right, bottom-right, bottom-left
     *
     * @param radii the x and y radii of the corners
     */
    var radii: FloatArray? = FloatArray(8)
        set(radii) {
            if (radii == null) {
                Arrays.fill(this.radii!!, 0f)
                mRadiiNonZero = false
            } else {
                checkArgument(radii.size == 8, "radii should have exactly 8 values")
                System.arraycopy(radii, 0, this.radii!!, 0, 8)
                mRadiiNonZero = false
                for (i in 0..7) {
                    mRadiiNonZero = mRadiiNonZero or (radii[i] > 0)
                }
            }
            mIsPathDirty = true
            invalidateSelf()
        }
    private val mScaleCornerRadii = FloatArray(8)

    private val mBorderRadii = FloatArray(8)

    private val mBounds = RectF()
    private val mPreBounds = RectF()
    private val mViewBounds = RectF()
    private val mPreViewBounds = RectF()
    private val mDrawBounds = RectF()

    private val mRootBounds = RectF()
    private val mPrevRootBounds = RectF()
    private val mBitmapBounds = RectF()

    private val mBoundsTransform = Matrix()
    private val mPrevBoundsTransform = Matrix()

    private val mParentTransform = Matrix()
    private val mPrevParentTransform = Matrix()
    private val mInverseParentTransform = Matrix()

    private val mTransform = Matrix()
    /**
     * Gets the border width.
     */
    private var borderWidth = 0f
        private set
    /**
     * Gets the border color.
     */
    private var borderColor = Color.TRANSPARENT
        private set
    /**
     * Gets the padding.
     */
    /**
     * Sets the padding for the bitmap.
     *
     * @param padding
     */
    private var padding = 0f
        set(padding) {
            if (this.padding != padding) {
                field = padding
                mIsPathDirty = true
                invalidateSelf()
            }
        }

    private val mPath = Path()
    private val mBorderPath = Path()
    private var mIsPathDirty = true
    private val mPaint = Paint()
    private val mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mIsShaderTransformDirty = true
    private var mLastBitmap: WeakReference<Bitmap>? = null
    private var mImageView: WeakReference<ImageView>? = null
    private var mScale: Float = 0.toFloat()


    init {
        mPaint.flags = Paint.ANTI_ALIAS_FLAG
        mBorderPaint.style = Paint.Style.STROKE
    }

    constructor(aImageView: ImageView, bitmap: Bitmap) : this(aImageView.resources, bitmap) {
        setImageView(aImageView)
    }

    fun setImageView(aImageView: ImageView?) {
        mImageView = null
        if (aImageView != null) {
            mImageView = WeakReference(aImageView)
        }
    }

    /**
     * Specify radius for the corners of the rectangle. If this is > 0, then the
     * drawable is drawn in a round-rectangle, rather than a rectangle.
     *
     * @param radius the radius for the corners of the rectangle
     */
    fun setRadius(radius: Float) {
        checkState(radius >= 0)
        Arrays.fill(radii!!, radius)
        mRadiiNonZero = radius != 0f
        mIsPathDirty = true
        invalidateSelf()
    }

    private fun checkState(expression: Boolean) {
        if (!expression) {
            throw IllegalStateException()
        }
    }

    private fun checkArgument(expression: Boolean, errorMessage: Any?) {
        if (!expression) {
            throw IllegalArgumentException(errorMessage.toString())
        }
    }

    private fun multiplyColorAlpha(color: Int, alpha: Int): Int {
        var alpha = alpha
        if (alpha == 255) {
            return color
        }
        if (alpha == 0) {
            return color and 0x00FFFFFF
        }
        alpha += (alpha shr 7) // make it 0..256
        val colorAlpha = color.ushr(24)
        val multipliedAlpha = colorAlpha * alpha shr 8
        return multipliedAlpha shl 24 or (color and 0x00FFFFFF)
    }

    /**
     * Sets the border
     *
     * @param color of the border
     * @param width of the border
     */
    fun setBorder(color: Int, width: Float) {
        if (!(borderColor == color && borderWidth == width)) {
            borderColor = color
            borderWidth = width
            mIsPathDirty = true
            invalidateSelf()
        }
    }

    override fun setAlpha(alpha: Int) {
        if (alpha != mPaint.alpha) {
            mPaint.alpha = alpha
            super.setAlpha(alpha)
            invalidateSelf()
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
        super.setColorFilter(colorFilter)
    }

    override fun draw(canvas: Canvas) {
        if (!shouldRound()) {
            super.draw(canvas)
            return
        }
        updateTransform()
        updatePath()
        updatePaint()
        val saveCount = canvas.save()
        canvas.concat(mInverseParentTransform)
        canvas.drawPath(mPath, mPaint)
        if (borderWidth > 0) {
            mBorderPaint.strokeWidth = borderWidth
            mBorderPaint.color = multiplyColorAlpha(borderColor, mPaint.alpha)
            canvas.drawPath(mBorderPath, mBorderPaint)
        }
        canvas.restoreToCount(saveCount)
    }

    /**
     * If both the radii and border width are zero, there is nothing to round.
     */

    internal fun shouldRound(): Boolean {
        return isCircle || mRadiiNonZero || borderWidth > 0
    }

    private fun updateTransform() {
        mParentTransform.reset()

        val view = mImageView!!.get()
        if (view != null) {
            mViewBounds.set(0f, 0f, view.measuredWidth.toFloat(), view.measuredHeight.toFloat())
        } else {
            mViewBounds.set(0f, 0f, 0f, 0f)
        }
        mBounds.set(bounds)
        if (mViewBounds != mPreViewBounds || mBounds != mPreBounds) {
            mIsPathDirty = true
            mPreViewBounds.set(mViewBounds)
            mPreBounds.set(mBounds)
            mScale = 1.0f
            if (view != null && !mViewBounds.isEmpty) {
                val scaleType = view.scaleType
                if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                    var dx = 0f
                    var dy = 0f
                    val dWidth = mBounds.width()
                    val dHeight = mBounds.height()
                    val vWidth = mViewBounds.width()
                    val vHeight = mViewBounds.height()
                    if (dWidth * vHeight > vWidth * dHeight) {
                        mScale = dHeight / vHeight
                        dx = (dWidth - vWidth * mScale) * 0.5f
                    } else {
                        mScale = dWidth / vWidth
                        dy = (dHeight - vHeight * mScale) * 0.5f
                    }
                    mRootBounds.set(dx, dy, dWidth - dx, dHeight - dy)
                } else {
                    mRootBounds.set(mBounds)
                }
            } else {
                mRootBounds.set(mBounds)
            }
        }
        mBitmapBounds.set(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        mDrawBounds.set(mBounds)
        mBoundsTransform.setRectToRect(mBitmapBounds, mDrawBounds, Matrix.ScaleToFit.FILL)

        if (mParentTransform != mPrevParentTransform || mBoundsTransform != mPrevBoundsTransform) {
            mIsShaderTransformDirty = true
            mParentTransform.invert(mInverseParentTransform)
            mTransform.set(mParentTransform)
            mTransform.preConcat(mBoundsTransform)
            mPrevParentTransform.set(mParentTransform)
            mPrevBoundsTransform.set(mBoundsTransform)
        }

        if (mRootBounds != mPrevRootBounds) {
            mIsPathDirty = true
            mPrevRootBounds.set(mRootBounds)
        }
    }

    private fun updatePath() {
        if (mIsPathDirty) {
            mBorderPath.reset()
            mRootBounds.inset(borderWidth / 2, borderWidth / 2)
            if (isCircle) {
                val radius = Math.min(mRootBounds.width(), mRootBounds.height()) / 2
                mBorderPath.addCircle(mRootBounds.centerX(), mRootBounds.centerY(), radius, Path.Direction.CW)
            } else {
                for (i in mScaleCornerRadii.indices) {
                    mScaleCornerRadii[i] = radii!![i] * mScale
                }
                for (i in mBorderRadii.indices) {
                    mBorderRadii[i] = mScaleCornerRadii[i] + padding - borderWidth / 2
                }
                mBorderPath.addRoundRect(mRootBounds, mBorderRadii, Path.Direction.CW)
            }
            mRootBounds.inset(-borderWidth / 2, -borderWidth / 2)

            mPath.reset()
            mRootBounds.inset(padding, padding)
            if (isCircle) {
                mPath.addCircle(
                        mRootBounds.centerX(),
                        mRootBounds.centerY(),
                        Math.min(mRootBounds.width(), mRootBounds.height()) / 2,
                        Path.Direction.CW)
            } else {
                mPath.addRoundRect(mRootBounds, mScaleCornerRadii, Path.Direction.CW)
            }
            mRootBounds.inset(-padding, -padding)
            mPath.fillType = Path.FillType.WINDING
            mIsPathDirty = false
        }
    }

    private fun updatePaint() {
        val bitmap = bitmap
        if (mLastBitmap == null || mLastBitmap!!.get() != bitmap) {
            mLastBitmap = WeakReference(bitmap)
            mPaint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mIsShaderTransformDirty = true
        }
        if (mIsShaderTransformDirty) {
            mPaint.shader.setLocalMatrix(mTransform)
            mIsShaderTransformDirty = false
        }
    }
}