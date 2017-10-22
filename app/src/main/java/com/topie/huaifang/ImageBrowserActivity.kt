package com.topie.huaifang

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kInflate
import com.topie.huaifang.extensions.kInsteadTo
import com.topie.huaifang.extensions.kParseUrlOrFileUri
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.imageloader.GlideApp
import com.topie.huaifang.view.photoview.PhotoView
import kotlinx.android.synthetic.main.base_title_layout.*

/**
 * Created by arman on 2017/10/21.
 * 大图预览
 */
class ImageBrowserActivity : HFBaseTitleActivity() {
    private var limit = 0
    private val selectList = mutableListOf<String>()
    private val imageList = mutableListOf<String>()
    private var viewPager: ViewPager? = null
    private val mAdapter = Adapter(imageList, selectList)

    companion object {
        const val EXTRA_LIMIT = "extra_limit"
        const val EXTRA_SELECT_LIST = "extra_select_list"
        const val EXTRA_CURRENT_POSITION = "extra_current_position"
        const val EXTRA_IMAGE_LIST = "extra_image_list"

        fun openImageBrowserActivity(context: Context, requestCode: Int, limit: Int, imageList: List<String>, selectList: List<String>?, currentPos: Int) {
            if (context !is Activity) {
                kToastShort("openImageScanActivity need a activity context")
                return
            }
            val intent = Intent(context, ImageBrowserActivity::class.java)
            intent.putExtra(EXTRA_LIMIT, limit)
            val selectArrayList = when (selectList) {
                null -> ArrayList()
                is ArrayList -> selectList
                else -> ArrayList(selectList)
            }
            val imageArrayList = when (imageList) {
                is ArrayList -> imageList
                else -> ArrayList(imageList)
            }
            intent.putStringArrayListExtra(EXTRA_SELECT_LIST, selectArrayList)
            intent.putStringArrayListExtra(EXTRA_IMAGE_LIST, imageArrayList)
            intent.putExtra(EXTRA_CURRENT_POSITION, currentPos)
            context.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        limit = intent.getIntExtra(EXTRA_LIMIT, 0)
        intent.getStringArrayListExtra(EXTRA_SELECT_LIST)?.kInsteadTo(selectList)
        intent.getStringArrayListExtra(EXTRA_IMAGE_LIST)?.kInsteadTo(imageList)
        viewPager = HFFixViewPager(this)
        setContentView(viewPager)
        viewPager!!.adapter = mAdapter
        mAdapter.onItemSelect = { v, position ->
            if (selectList.contains(imageList[position])) {
                selectList.remove(imageList[position])
                v.isSelected = false
            } else {
                if (selectList.size < limit) {
                    selectList.add(imageList[position])
                    v.isSelected = true
                } else {
                    kToastShort("只能选择${limit}张")
                }
            }
            initTitle()
        }
        viewPager!!.currentItem = intent.getIntExtra(EXTRA_CURRENT_POSITION, 0)
        mAdapter.notifyDataSetChanged()
        tv_base_title_right.setOnClickListener {
            val intent = Intent()
            if (selectList.isEmpty()) {
                selectList.add(imageList[viewPager!!.currentItem])
            }
            intent.putStringArrayListExtra(EXTRA_SELECT_LIST, ArrayList(selectList))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        initTitle()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.onItemSelect = null
    }

    private fun initTitle() {
        setBaseTitle("${viewPager?.currentItem}/${imageList.size}")
        if (selectList.isEmpty()) {
            setBaseTitleRight("确定")
        } else {
            setBaseTitleRight("确定(${selectList.size}/${limit})")
        }
    }

    class Adapter(private val imageList: List<String>, private val selectList: List<String>) : PagerAdapter() {

        var onItemSelect: ((v: View, position: Int) -> Unit)? = null

        override fun isViewFromObject(view: View?, obj: Any?): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return imageList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val kInflate = container.kInflate(R.layout.image_browser_item)
            kInflate.findViewById<View>(R.id.iv_content_select).setOnClickListener {
                onItemSelect?.invoke(it, it.tag as Int)
            }
            val ivSelect = kInflate.findViewById<View>(R.id.iv_content_select)
            val photoView = kInflate.findViewById<PhotoView>(R.id.pv_content)
            ivSelect.isSelected = selectList.contains(imageList[position])
            ivSelect.tag = position
            GlideApp.with(container).asBitmap().load(imageList[position].kParseUrlOrFileUri()).into(photoView)
            container.addView(kInflate)
            return kInflate
        }

        override fun destroyItem(container: ViewGroup?, position: Int, obj: Any?) {
            container?.removeView(obj as View?)
        }
    }

    class HFFixViewPager @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : ViewPager(context, attrs) {

        override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
            return try {
                super.dispatchTouchEvent(ev)
            } catch (e: Exception) {
                //do nothing
                false
            }
        }
    }
}