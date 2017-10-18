package com.topie.huaifang.imageloader

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kParseUrl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.image_scan_activity.*

/**
 * Created by arman on 2017/10/18.
 * 图片浏览框架
 */
class ImageScanActivity : AppCompatActivity() {


    private val contentAdapter: ContentAdapter = ContentAdapter(null)
    private val slidingMenuAdapter: SlidingMenuAdapter = SlidingMenuAdapter(contentAdapter, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        rv_content.layoutManager = LinearLayoutManager(this)
        rv_content.adapter = contentAdapter

        getLocalImageSet(this).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            val arrayList = ArrayList<Pair<String, List<String>>>()
            arrayList.add("all".to(it.all))
            it.tree.forEach {
                arrayList.add(it.toPair())
            }
            slidingMenuAdapter.menuList = arrayList
            slidingMenuAdapter.notifyDataSetChanged()
        }, {
            Toast.makeText(baseContext, it.message, Toast.LENGTH_LONG).show()
        })
    }

    private class SlidingMenuAdapter(val contentAdapter: ContentAdapter, var menuList: List<Pair<String, List<String>>>?) : RecyclerView.Adapter<SlidingMenuAdapter.ViewHolder>() {
        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.textView?.text = menuList?.get(position)?.first
            holder?.tvCount?.text = (menuList?.get(position)?.second?.size ?: 0).toString() + "张"
            holder?.imageView?.loadImageUri(menuList?.get(position)?.second?.firstOrNull()?.kParseUrl())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflate = LayoutInflater.from(parent.context).inflate(R.layout.image_scan_menu_item, parent, false)
            val viewHolder = ViewHolder(inflate)
            inflate.setOnClickListener {
                contentAdapter.list = menuList?.get(viewHolder.adapterPosition)?.second
                contentAdapter.notifyDataSetChanged()
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

    private class ContentAdapter(var list: List<String>?) : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.imageView?.loadImageUri(list?.get(position)?.kParseUrl())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflate = LayoutInflater.from(parent.context).inflate(R.layout.image_scan_content_item, parent, false)
            return ViewHolder(inflate)
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }

        private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: HFImageView = itemView.findViewById(R.id.iv_content)
        }
    }

}