package com.topie.huaifang.imageloader

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kInsteadTo
import com.topie.huaifang.extensions.kParseFileUri
import com.topie.huaifang.extensions.kToastShort
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.image_scan_activity.*

/**
 * Created by arman on 2017/10/18.
 * 图片浏览框架
 */
class ImageScanActivity : AppCompatActivity() {


    private var limit = 0
    private val selectList = mutableListOf<String>()
    private val contentAdapter: ContentAdapter = ContentAdapter(selectList, null)
    private val slidingMenuAdapter: SlidingMenuAdapter = SlidingMenuAdapter(null)

    companion object {
        const val EXTRA_LIMIT = "extra_limit"
        const val EXTRA_SELECT_LIST = "extra_select_list"

        fun openImageScanActivity(context: Context, requestCode: Int, limit: Int, selectList: List<String>?) {
            if (context !is Activity) {
                kToastShort("openImageScanActivity need a activity context")
                return
            }
            val intent = Intent(context, ImageScanActivity::class.java)
            intent.putExtra(EXTRA_LIMIT, limit)
            val arrayList = when (selectList) {
                null -> ArrayList()
                is ArrayList -> selectList
                else -> ArrayList(selectList)
            }
            intent.putStringArrayListExtra(EXTRA_SELECT_LIST, arrayList)
            context.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        limit = intent.getIntExtra(EXTRA_LIMIT, 0)
        intent.getStringArrayListExtra(EXTRA_SELECT_LIST)?.kInsteadTo(selectList)
        setContentView(R.layout.image_scan_activity)
        setSupportActionBar(toolbar)
        toolbar.title = "图片选择"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        drawerToggle.syncState()
        drawerLayout.addDrawerListener(drawerToggle)

        rv_menu.layoutManager = LinearLayoutManager(this)
        rv_menu.adapter = slidingMenuAdapter
        rv_content.layoutManager = GridLayoutManager(this, 3)
        rv_content.adapter = contentAdapter

        getLocalImageSet(this).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            val arrayList = ArrayList<Pair<String, List<String>>>()
            arrayList.add("全部".to(it.all))
            it.tree.forEach {
                arrayList.add(it.toPair())
            }
            slidingMenuAdapter.menuList = arrayList
            slidingMenuAdapter.notifyDataSetChanged()
            contentAdapter.list = it.all
            contentAdapter.notifyDataSetChanged()
            toolbar.title = arrayList.first().first
        }, {
            Toast.makeText(baseContext, it.message, Toast.LENGTH_LONG).show()
        })
        slidingMenuAdapter.onItemClick = {
            //改变对应内容
            contentAdapter.list = slidingMenuAdapter.menuList?.get(it)?.second
            contentAdapter.notifyDataSetChanged()
            //改变menu选择选项
            val lastSelect = slidingMenuAdapter.selected
            slidingMenuAdapter.selected = it
            slidingMenuAdapter.notifyItemChanged(lastSelect)
            slidingMenuAdapter.notifyItemChanged(it)
            //改变title
            toolbar.title = slidingMenuAdapter.menuList?.get(it)?.first
            drawerLayout.closeDrawers()
        }
        contentAdapter.onItemSelectClick = { v, path ->
            if (path == null) {
                kToastShort("图片地址错误")
            } else if (selectList.contains(path)) {
                v.isSelected = false
                selectList.removeAll { it == path }
            } else {
                if (limit < selectList.size) {
                    v.isSelected = true
                    selectList.add(path)
                } else {
                    kToastShort("只能选择${limit}张")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        contentAdapter.onItemClick = null
        contentAdapter.onItemSelectClick = null
        slidingMenuAdapter.onItemClick = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_scan, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (R.id.action_confirm == item?.itemId) {
            val intent = Intent()
            intent.putStringArrayListExtra(EXTRA_SELECT_LIST, ArrayList(selectList))
            setResult(Activity.RESULT_OK, intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(Gravity.LEFT) == true) {
            drawerLayout.closeDrawers()
        } else {
            setResult(Activity.RESULT_CANCELED)
            super.onBackPressed()
        }
    }

    private class SlidingMenuAdapter(var menuList: List<Pair<String, List<String>>>?) : RecyclerView.Adapter<SlidingMenuAdapter.ViewHolder>() {

        var onItemClick: ((position: Int) -> Unit)? = null
        var selected: Int = 0

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.textView?.text = menuList?.get(position)?.first
            holder?.tvCount?.text = (menuList?.get(position)?.second?.size ?: 0).toString() + "张"
            holder?.imageView?.loadImageUri(menuList?.get(position)?.second?.firstOrNull()?.kParseFileUri())
            holder?.itemView?.isSelected = position == selected
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflate = LayoutInflater.from(parent.context).inflate(R.layout.image_scan_menu_item, parent, false)
            val viewHolder = ViewHolder(inflate)
            inflate.setOnClickListener {
                onItemClick?.invoke(viewHolder.adapterPosition)
            }
            return viewHolder
        }

        override fun getItemCount(): Int {
            return menuList?.size ?: 0
        }

        private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.tv_menu_item)
            val tvCount: TextView = itemView.findViewById(R.id.tv_menu_item_count)
            val imageView: HFImageView = itemView.findViewById(R.id.iv_menu_item)
        }
    }

    private class ContentAdapter(val selectList: List<String>, var list: List<String>?) : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

        var onItemClick: ((position: Int) -> Unit)? = null
        var onItemSelectClick: ((v: View, path: String?) -> Unit)? = null

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.imageView?.loadImageUri(list?.get(position)?.kParseFileUri())
            holder?.ivSelect?.isSelected = selectList.contains(list?.get(position))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflate = LayoutInflater.from(parent.context).inflate(R.layout.image_scan_content_item, parent, false)
            val viewHolder = ViewHolder(inflate)
            inflate.setOnClickListener { onItemClick?.invoke(viewHolder.adapterPosition) }
            viewHolder.ivSelect.setOnClickListener {
                onItemSelectClick?.invoke(it, list?.get(viewHolder.adapterPosition))
            }
            return viewHolder
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }

        private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: HFImageView = itemView.findViewById(R.id.iv_content)
            val ivSelect: ImageView = itemView.findViewById(R.id.iv_content_select)
        }
    }

}