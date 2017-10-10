package com.topie.huaifang.function.live

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kStartActivity
import kotlinx.android.synthetic.main.base_title_layout.*
import kotlinx.android.synthetic.main.function_guide_activity.*

/**
 * Created by arman on 2017/10/10.
 * 社区集市
 */
class HFFunLiveBazaarActivity : HFBaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_guide_activity)
        setBaseTitle(R.string.facing_index_fun_bazaar)
        setBaseTitleRight("发布")
        tv_base_title_right.setOnClickListener {
            this@HFFunLiveBazaarActivity.kStartActivity(HFFunLiveBazaarApplyActivity::class.java)
        }
        val titleList: ArrayList<String> = arrayListOf()
        titleList.add("跳骚市场")
        titleList.add("车位共享")
        val vpAdapter = VPAdapter(titleList, supportFragmentManager)
        vp_fun_guide.adapter = vpAdapter
        tl_fun_guide.setupWithViewPager(vp_fun_guide)
        tl_fun_guide.tabMode = TabLayout.MODE_FIXED
        vpAdapter.notifyDataSetChanged()
    }


    class VPAdapter(private val titleList: List<String>, fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> HFFunLiveBazaarFragment.newInstance(0)
                else -> HFFunLiveBazaarFragment.newInstance(1)
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