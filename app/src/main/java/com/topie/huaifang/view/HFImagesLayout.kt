package com.topie.huaifang.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kClone
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.util.HFDimensUtils

/**
 * Created by arman on 2017/10/20.
 * 上传图片的视图UI
 */
class HFImagesLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val onItemClickListener: OnItemClickListenerImpl = OnItemClickListenerImpl()
    private val controller = ImageUploadController()
    private val mAdapter = Adapter(onItemClickListener, controller)

    init {
        val hfCrazyGridView = HFCrazyGridView(context)
        hfCrazyGridView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        hfCrazyGridView.numColumns = 4
        hfCrazyGridView.horizontalSpacing = HFDimensUtils.dp2px(10.toFloat())
        hfCrazyGridView.verticalSpacing = HFDimensUtils.dp2px(10.toFloat())
        hfCrazyGridView.adapter = mAdapter
        addView(hfCrazyGridView)
    }

    fun setPathList(list: List<Uri>?) {
        if (list == null || list.isEmpty()) {
            controller.repairsImages.clear()
            mAdapter.notifyDataSetChanged()
        } else {
            val kClone = controller.repairsImages.kClone()
            controller.repairsImages.clear()
            list.forEach { uri ->
                val imageUpload = kClone.firstOrNull({ it.uri == uri }) ?: ImageUpload(uri)
                controller.repairsImages.add(imageUpload)
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    fun addPath(uri: Uri) {
        val firstOrNull = controller.repairsImages.firstOrNull({ it.uri == uri })
        if (firstOrNull == null) {
            controller.repairsImages.add(ImageUpload(uri))
            mAdapter.notifyDataSetChanged()
        }
    }

    fun getImageSize(): Int {
        return controller.repairsImages.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener.base = onItemClickListener
    }

    private class OnItemClickListenerImpl : OnItemClickListener {

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

    private class Adapter(val onItemClickListener: OnItemClickListener, val controller: ImageUploadController) : BaseAdapter() {

        companion object {
            const val TYPE_ADD = -1
            const val TYPE_NORMAL = 1
        }

        private val list = controller.repairsImages

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val imageItem: ImageItem = if (convertView == null) {
                val imageItem = ImageItem(parent.context)
                imageItem.layoutParams = AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, getItemViewType(position))
                imageItem.setBackgroundColor(Color.WHITE)
                imageItem.setOnClickListener {
                    val pos = it.tag as Int
                    if (getItemViewType(pos) == TYPE_ADD) {
                        onItemClickListener.onAdd()
                    } else {
                        onItemClickListener.onImageClicked(getItem(pos)?.uri, pos)
                    }
                }
                imageItem
            } else {
                convertView as ImageItem
            }
            imageItem.tag = position
            if (getItemViewType(position) == TYPE_ADD) {
                imageItem.hfImageView.setImageResource(R.mipmap.ic_image_upload_add)
                imageItem.hfImageView.mDefImageRes = R.mipmap.ic_image_upload_add
            } else {
                imageItem.hfImageView.loadImageUri(getItem(position)?.uri)
            }
            val item = getItem(position)
            when (item?.state) {
                ImageUpload.STATE_UPDATING -> {
                    imageItem.ivState.visibility = View.VISIBLE
                    imageItem.startStateAni()
                }
                ImageUpload.STATE_FAILURE -> {
                    imageItem.stopStateAni()
                    imageItem.ivState.visibility = View.VISIBLE
                }
                else -> {
                    imageItem.ivState.visibility = View.GONE
                    imageItem.stopStateAni()
                }
            }
            return imageItem
        }

        override fun getItem(position: Int): ImageUpload? {
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
            return list.size
        }
    }

    private class ImageItem constructor(context: Context) : RelativeLayout(context) {

        val hfImageView = HFImageView(context)
        val ivState = ImageView(context)
        private val animatable: Animatable

        init {
            hfImageView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            hfImageView.setBackgroundColor(Color.WHITE)
            hfImageView.setAspectRatio(1.toFloat())
            hfImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            hfImageView.id = R.id.id_image_upload
            addView(hfImageView)

            val lp = LayoutParams(HFDimensUtils.dp2px(40.toFloat()), HFDimensUtils.dp2px(40.toFloat()))
            lp.addRule(CENTER_IN_PARENT)
            lp.addRule(ALIGN_LEFT, R.id.id_image_upload)
            lp.addRule(ALIGN_TOP, R.id.id_image_upload)
            lp.addRule(ALIGN_RIGHT, R.id.id_image_upload)
            lp.addRule(ALIGN_BOTTOM, R.id.id_image_upload)
            ivState.layoutParams = lp
            ivState.setBackgroundColor(0X33E1E1E1)
            ivState.scaleType = ImageView.ScaleType.CENTER_CROP
            ivState.setImageResource(R.drawable.ani_image_upload)
            animatable = ivState.drawable as Animatable
            addView(ivState)
        }

        fun startStateAni() {
            animatable.start()
        }

        fun stopStateAni() {
            animatable.stop()
        }

    }

    private class ImageUploadController {

        val repairsImages: MutableList<ImageUpload> = arrayListOf()

    }

    private class ImageUpload(val uri: Uri?) {

        var state: Int = STATE_COMPLETE

        companion object {
            const val STATE_PREPARE = 0
            const val STATE_UPDATING = 1
            const val STATE_COMPLETE = 2
            const val STATE_FAILURE = 3
        }
    }
}