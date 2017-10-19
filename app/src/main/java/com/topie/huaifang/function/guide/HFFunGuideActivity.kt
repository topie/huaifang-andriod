package com.topie.huaifang.function.guide

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunGuideMenuResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.function_guide_activity.*

/**
 * Created by arman on 2017/9/21.
 * 办事指南
 */
class HFFunGuideActivity : HFBaseTitleActivity() {

    private var vpAdapter: VPAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_guide_activity)
        setBaseTitle(R.string.facing_index_fun_guide)
        vpAdapter = VPAdapter(supportFragmentManager)
        vp_fun_guide.adapter = vpAdapter
        tl_fun_guide.setupWithViewPager(vp_fun_guide)
    }

    override fun onResume() {
        super.onResume()
        HFRetrofit.hfService.getFunGuideMenu().subscribeResultOkApi {
            vpAdapter?.list = it.data?.data
            tl_fun_guide.tabMode = when {
                vpAdapter?.count ?: 0 > 3 -> TabLayout.MODE_SCROLLABLE
                else -> TabLayout.MODE_FIXED
            }
            vpAdapter?.notifyDataSetChanged()
        }.kInto(pauseDisableList)
    }


    class VPAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var list: List<HFFunGuideMenuResponseBody.ListData>? = null

        override fun getItem(position: Int): Fragment {
            return HFFunGuideFragment.newInstance(list?.get(position))
        }

        override fun getCount(): Int {
            return list?.size ?: 0
        }

        override fun getPageTitle(position: Int): CharSequence {
            return list?.get(position)?.title ?: position.toString()
        }

    }
}