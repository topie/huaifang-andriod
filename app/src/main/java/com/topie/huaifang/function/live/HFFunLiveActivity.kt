package com.topie.huaifang.function.live

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFLiveListResponseBody
import com.topie.huaifang.http.subscribeApi
import kotlinx.android.synthetic.main.function_guide_activity.*

/**
 * Created by arman on 2017/9/21.
 * 居务公开
 */
class HFFunLiveActivity : HFBaseTitleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_guide_activity)
        setBaseTitle(R.string.facing_index_fun_live)
        HFRetrofit.hfService.getFunLiveList().subscribeApi {
            val vpAdapter = VPAdapter()
            vpAdapter.list = it.data?.data
            vp_fun_guide.adapter = vpAdapter
            tl_fun_guide.setupWithViewPager(vp_fun_guide)
            vpAdapter.notifyDataSetChanged()
        }
    }


    class VPAdapter : PagerAdapter() {

        var list: List<HFLiveListResponseBody.ListData>? = null

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view == any
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflate = LayoutInflater.from(container.context).inflate(R.layout.function_live_list, container, false)
            val textView: TextView = inflate.kFindViewById(R.id.tv_fun_live_list_item)
            textView.text = getListData(position)?.content?.let { Html.fromHtml(it) }
            container.addView(inflate)
            return inflate
        }

        private fun getListData(position: Int): HFLiveListResponseBody.ListData? {
            return list?.takeIf { it.size > position }?.get(position)
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            container.removeView(any as View)
        }

        override fun getCount(): Int {
            return list?.size ?: 0
        }

        override fun getPageTitle(position: Int): CharSequence {
            return getListData(position)?.title ?: position.toString()
        }

    }
}