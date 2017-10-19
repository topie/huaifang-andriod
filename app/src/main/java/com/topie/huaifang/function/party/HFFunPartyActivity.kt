package com.topie.huaifang.function.party

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.extensions.log
import kotlinx.android.synthetic.main.base_title_layout.*
import kotlinx.android.synthetic.main.function_guide_activity.*

/**
 * Created by arman on 2017/9/21.
 * 社区党建
 */
class HFFunPartyActivity : HFBaseTitleActivity() {

    companion object {
        /**
         * 党支部活动
         */
        const val POSITION_ZHIBU = 0
        /**
         * 党务公开
         */
        const val POSITION_PUB = 1
        /**
         * 党员信息
         */
        const val POSITION_MEMBERS = 2

        const val EXTRA_POSITION = "extra_position"
    }

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
        vp_fun_guide.setCurrentItem(intent?.getIntExtra(EXTRA_POSITION, POSITION_ZHIBU) ?: POSITION_ZHIBU, false)
        vp_fun_guide.addOnPageChangeListener(mListener)
        tv_base_title_right.setOnClickListener {
            when (vp_fun_guide?.currentItem) {
                0 -> this@HFFunPartyActivity.kStartActivity(HFFunPartyActPublishActivity::class.java)
                else -> log("not support position [${vp_fun_guide?.currentItem}]")
            }
        }
        mListener.onPageSelected(vp_fun_guide.currentItem)
    }

    private val mListener = object : SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            tv_base_title_right.text = when (position) {
                0 -> "发布"
                else -> null
            }

        }
    }


    class VPAdapter(private val titleList: List<String>, fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> HFFunPartyActFragment()
                1 -> HFFunPartyPublicFragment()
                2 -> HFFunPartyMembersFragment()
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