package com.topie.huaifang.function.notice

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.HFBaseTitleActivity
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kReleaseSelf
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.composeApi
import kotlinx.android.synthetic.main.function_guide_activity.*

/**
 * Created by arman on 2017/9/21.
 * 通知公告
 */
class HFFunPublicActivity : HFBaseTitleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_guide_activity)
        setBaseTitle(R.string.facing_index_fun_announcement)
        val titleList: ArrayList<String> = arrayListOf()
        titleList.add("物业公告")
        titleList.add("社区公告")
        val vpAdapter = VPAdapter(titleList)
        vp_fun_guide.adapter = vpAdapter
        tl_fun_guide.setupWithViewPager(vp_fun_guide)
        tl_fun_guide.tabMode = TabLayout.MODE_FIXED
        vpAdapter.notifyDataSetChanged()
    }


    class VPAdapter(val titleList: List<String>) : PagerAdapter() {

        private val list: SparseArray<ViewHolder> = SparseArray()

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view == any
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val viewHolder = list[position] ?: let {
                val inflate = LayoutInflater.from(container.context).inflate(R.layout.function_public_list_item, container, false)
                val viewHolder = ViewHolder(inflate, position)
                list.put(position, viewHolder)
                return@let viewHolder
            }
            viewHolder.itemView.kReleaseSelf()
            container.addView(viewHolder.itemView)
            return viewHolder.itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            container.removeView(any as View)
        }

        override fun getCount(): Int {
            return titleList.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList[position]
        }

    }

    class ViewHolder(val itemView: View, val position: Int) {

        private val textView = itemView.findViewById<TextView>(R.id.tv_fun_public_list_item)

        init {
            textView.text = "item{$position}"
            HFRetrofit.hfService.getFunPublicList(position).composeApi().subscribe({

            }, {

            })
        }
    }
}