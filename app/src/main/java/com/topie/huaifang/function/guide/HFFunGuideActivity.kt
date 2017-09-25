package com.topie.huaifang.function.guide

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.topie.huaifang.HFBaseTitleActivity
import com.topie.huaifang.R
import com.topie.huaifang.extensions.log
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunGuideMenuResponseBody
import com.topie.huaifang.http.composeApi
import kotlinx.android.synthetic.main.function_guide_activity.*

/**
 * Created by arman on 2017/9/21.
 * 办事指南
 */
class HFFunGuideActivity : HFBaseTitleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_guide_activity)
        setBaseTitle(R.string.facing_index_fun_guide)
        HFRetrofit.hfService.getFunGuideMenu().composeApi().subscribe({
            val intRange = 0..10
            for (i in intRange) {
                tl_fun_guide.addTab(tl_fun_guide.newTab().setText("title{$i}"))
            }

            val list: MutableList<HFFunGuideMenuResponseBody.ListData> = arrayListOf()
            for (i in intRange) {
                list.add(HFFunGuideMenuResponseBody.ListData())
            }
            val vpAdapter = VPAdapter(supportFragmentManager)
            vpAdapter.list = list
            vp_fun_guide.adapter = vpAdapter
            tl_fun_guide.setupWithViewPager(vp_fun_guide)
            vpAdapter.notifyDataSetChanged()
        }, {

        })
    }


    class VPAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var list: List<HFFunGuideMenuResponseBody.ListData>? = null

        override fun getItem(position: Int): Fragment {
            log("getItem[$position]")
            val hfFunGuideFragment = HFFunGuideFragment()
            hfFunGuideFragment.listData = list?.get(position)
            return hfFunGuideFragment
        }

        override fun getCount(): Int {
            return list?.size ?: 0
        }

        override fun getPageTitle(position: Int): CharSequence {
            return "title{$position}"
        }

    }
}