package com.topie.huaifang.function.party

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.topie.huaifang.HFBaseTitleActivity
import com.topie.huaifang.R
import kotlinx.android.synthetic.main.function_guide_activity.*

/**
 * Created by arman on 2017/9/21.
 * 社区党建
 */
class HFFunPartyActivity : HFBaseTitleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_guide_activity)
        setBaseTitle(R.string.facing_index_fun_party)
        val titleList: ArrayList<String> = arrayListOf()
        titleList.add("党支部活动")
        titleList.add("党务公开")
        titleList.add("党员信息建设")
        val vpAdapter = VPAdapter(titleList, supportFragmentManager)
        vp_fun_guide.adapter = vpAdapter
        tl_fun_guide.setupWithViewPager(vp_fun_guide)
        tl_fun_guide.tabMode = TabLayout.MODE_FIXED
        vpAdapter.notifyDataSetChanged()
    }


    class VPAdapter(private val titleList: List<String>, fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> HFFunPartyActFragment()
                1 -> HFFunPartyPublicFragment()
                2 -> HFFunPartyActFragment()
                else -> HFFunPartyActFragment()
            }
        }

        override fun getCount(): Int {
            return titleList.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList[position]
        }

    }
}