package com.topie.huaifang.view

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kReset
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.util.HFDimensUtils

/**
 * Created by arman on 2017/10/20.
 * 上传图片的视图UI
 */
class HFImagesUploadLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val onItemClickListener: OnItemClickListenerImpl = OnItemClickListenerImpl()
    private val mAdapter = Adapter(onItemClickListener)

    init {
        val hfCrazyGridView = HFCrazyGridView(context)
        hfCrazyGridView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        hfCrazyGridView.numColumns = 4
        hfCrazyGridView.horizontalSpacing = HFDimensUtils.dp2px(10.toFloat())
        hfCrazyGridView.verticalSpacing = HFDimensUtils.dp2px(10.toFloat())
        hfCrazyGridView.adapter = mAdapter
        addView(hfCrazyGridView)
    }

    fun setUriList(list: List<Uri?>) {
        mAdapter.list.kReset(list)
        mAdapter.notifyDataSetChanged()
    }

    fun addUri(uri: Uri?) {
        mAdapter.list.add(uri)
        mAdapter.notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener.base = onItemClickListener
    }

    class OnItemClickListenerImpl : OnItemClickListener {

        var base: OnItemClickListener? = null

        override fun onAdd() {
            base?.onAdd()
        }

        override fun onImageClicked(uri: Uri?, position: Int) {
            base?.onImageClicked(uri, position)
        }

    }

    interface OnItemClickListener {
        fun onAdd()
        fun onImageClicked(uri: Uri?, position: Int)
    }

    private class Adapter(val onItemClickListener: OnItemClickListener, val list: MutableList<Uri?> = mutableListOf()) : BaseAdapter() {

        companion object {
            const val TYPE_ADD = -1
            const val TYPE_NORMAL = 1
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            return if (convertView == null) {
                val hfImageView = HFImageView(parent.context)
                hfImageView.layoutParams = AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, getItemViewType(position))
                hfImageView.setBackgroundColor(Color.WHITE)
                hfImageView.setAspectRatio(1.toFloat())
                hfImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                if (getItemViewType(position) == TYPE_ADD) {
                    hfImageView.setImageResource(R.mipmap.ic_image_upload_add)
                    hfImageView.mDefImageRes = R.mipmap.ic_image_upload_add
                } else {
                    hfImageView.loadImageUri(getItem(position))
                    hfImageView.setOnClickListener { onItemClickListener.onImageClicked(getItem(position), position) }
                }
                hfImageView.setOnClickListener {
                    val pos = it.tag as Int
                    if (getItemViewType(pos) == TYPE_ADD) {
                        onItemClickListener.onAdd()
                    } else {
                        onItemClickListener.onImageClicked(getItem(pos), pos)
                    }
                }
                hfImageView.tag = position
                hfImageView
            } else {
                (convertView as HFImageView).loadImageUri(getItem(position))
                convertView
            }
        }

        override fun getItem(position: Int): Uri? {
            return when {
                position < list.size -> list[position]
                else -> null
            }
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return when {
                position < list.size -> TYPE_NORMAL
                else -> TYPE_ADD
            }
        }

        override fun getCount(): Int {
            return (list.size + 1).coerceAtMost(8)
        }
    }
}