package com.topie.huaifang.function.live

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.HFBaseTitleActivity
import com.topie.huaifang.R
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFLiveListResponseBody
import com.topie.huaifang.http.composeApi
import kotlinx.android.synthetic.main.function_guide_activity.*

/**
 * Created by arman on 2017/9/21.
 * 办事指南
 */
class HFFunLiveActivity : HFBaseTitleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_guide_activity)
        setBaseTitle(R.string.facing_index_fun_live)
        HFRetrofit.hfService.getFunLiveMenu().composeApi().subscribe({
            val intRange = 0..10
            for (i in intRange) {
                tl_fun_guide.addTab(tl_fun_guide.newTab().setText("title{$i}"))
            }
            val list: MutableList<HFLiveListResponseBody.BodyData> = arrayListOf()
            for (i in intRange) {
                list.add(HFLiveListResponseBody.BodyData())
            }
            val vpAdapter = VPAdapter()
            vpAdapter.list = list
            vp_fun_guide.adapter = vpAdapter
            tl_fun_guide.setupWithViewPager(vp_fun_guide)
            vpAdapter.notifyDataSetChanged()
        }, {

        })
    }


    class VPAdapter : PagerAdapter() {

        var list: List<HFLiveListResponseBody.BodyData>? = null

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view == any
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflate = LayoutInflater.from(container.context).inflate(R.layout.function_guide_list, container, false)
            val textView = inflate.findViewById<TextView>(R.id.tv_fun_guide_list_item)
            textView.text = "item{$position}"
            container.addView(inflate)
            return inflate
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            container.removeView(any as View)
        }

        override fun getCount(): Int {
            return list?.size ?: 0
        }

        override fun getPageTitle(position: Int): CharSequence {
            return "title{$position}"
        }

    }
}