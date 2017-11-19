package com.topie.huaifang.function.notice

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
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
        titleList.add("社区公告")
        titleList.add("物业公告")
        titleList.add("村务公告")
        val vpAdapter = VPAdapter(titleList, supportFragmentManager)
        vp_fun_guide.adapter = vpAdapter
        tl_fun_guide.setupWithViewPager(vp_fun_guide)
        tl_fun_guide.tabMode = TabLayout.MODE_FIXED
        vpAdapter.notifyDataSetChanged()
    }


    class VPAdapter(private val titleList: List<String>, fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return HFFunNoteFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return titleList.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList[position]
        }
    }
}