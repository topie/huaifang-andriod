package com.topie.huaifang.function.library

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kGet
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.function_guide_activity.*

/**
 * Created by arman on 2017/10/2.
 * 社区图书馆
 */
class HFFunLibraryActivity : HFBaseTitleActivity() {

    private var adapter: VPAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_guide_activity)
        setBaseTitle(R.string.facing_index_fun_library)
        adapter = VPAdapter(supportFragmentManager)
        vp_fun_guide.adapter = adapter
        tl_fun_guide.setupWithViewPager(vp_fun_guide)
        tl_fun_guide.tabMode = TabLayout.MODE_SCROLLABLE
        adapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        HFRetrofit.hfService.getFunLibraryMenus().subscribeResultOkApi {
            it.data?.data?.also {
                tl_fun_guide?.tabMode = if (it.size > 4) {
                    TabLayout.MODE_SCROLLABLE
                } else {
                    TabLayout.MODE_FIXED
                }
                adapter?.setAllTitle(it)
                adapter?.notifyDataSetChanged()
            }
        }.kInto(pauseDisableList)
    }

    class VPAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val titleList: MutableList<String> = arrayListOf()

        fun setAllTitle(list: List<String>) {
            titleList.clear()
            titleList.addAll(list)
        }

        override fun getItem(position: Int): Fragment {
            return HFFunLibraryFragment.newInstance(titleList.kGet(position) ?: "")
        }

        override fun getCount(): Int {
            return titleList.size
        }

        override fun getItemId(position: Int): Long {
            return titleList.kGet(position)?.hashCode()?.toLong() ?: position.toLong()
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList.kGet(position) ?: position.toString()
        }

    }
}