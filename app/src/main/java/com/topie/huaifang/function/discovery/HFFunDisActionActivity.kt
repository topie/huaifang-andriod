package com.topie.huaifang.function.discovery

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.extensions.kStartActivity
import kotlinx.android.synthetic.main.base_title_layout.*
import kotlinx.android.synthetic.main.function_guide_activity.*

/**
 * Created by arman on 2017/10/23.
 * 发现--活动
 */
class HFFunDisActionActivity : HFBaseTitleActivity() {
    private var vpAdapter: VPAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_guide_activity)
        setBaseTitle(R.string.facing_discovery_action)
        setBaseTitleRight("发布")
        tv_base_title_right.setOnClickListener {
            this@HFFunDisActionActivity.kStartActivity(HFFunDisActionPublishActivity::class.java)
        }
        vpAdapter = VPAdapter(supportFragmentManager)
        vp_fun_guide.adapter = vpAdapter
        tl_fun_guide.setupWithViewPager(vp_fun_guide)
        tl_fun_guide.tabMode = TabLayout.MODE_FIXED
        //初始化数据
        val list = mutableListOf<Pair<Int, String>>()
        0.to("全部活动").kInto(list)
//        1.to("槐房社团").kInto(list)
        2.to("我的发布").kInto(list)
//        3.to("已参加活动").kInto(list)
        vpAdapter!!.list = list
        vpAdapter!!.notifyDataSetChanged()
    }


    class VPAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var list: List<Pair<Int, String>>? = null

        override fun getItem(position: Int): Fragment {
            return HFFunDisActionFragment.newInstance(list?.get(position)?.first ?: -1)
        }

        override fun getCount(): Int {
            return list?.size ?: 0
        }

        override fun getPageTitle(position: Int): CharSequence {
            return list?.get(position)?.second ?: position.toString()
        }

    }
}