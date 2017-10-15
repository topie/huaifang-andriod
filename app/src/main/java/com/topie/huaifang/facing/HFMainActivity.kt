package com.topie.huaifang.facing

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kGetIdentifier
import com.topie.huaifang.extensions.log
import kotlinx.android.synthetic.main.base_title_layout.*


class HFMainActivity : HFBaseTitleActivity() {

    private var currentTabPosition = -1

    private val facingFragments: List<Pair<Class<out Fragment>, String>> = arrayListOf(
            HFIndexFragment::class.java.to("首页"),
            HFMsgFragment::class.java.to("消息"),
            HFDiscoveryFragment::class.java.to("发现"),
            HFMineFragment::class.java.to("我")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.facing_main_activity)
        iv_base_title_back.visibility = View.GONE
        mContentId = R.id.fl_facing_frag
        initBottomView(savedInstanceState)
    }

    private fun initBottomView(savedInstanceState: Bundle?) {
        val listener: View.OnClickListener = View.OnClickListener {
            val index = it.getTag(R.id.id_facing_bottom_index) ?: 0
            selectTab(index as Int)
        }
        val tabIndex = savedInstanceState?.getInt("index_bottom_tab_index") ?: 0
        for (i in 0..3) {
            val layoutId = kGetIdentifier("ll_facing_bottom_tab_$i", "id")
            val drawableId = kGetIdentifier("ic_facing_bottom_tab_$i", "drawable")
            val stringId = kGetIdentifier("facing_bottom_tab_$i", "string")
            val layout = kFindViewById<View>(layoutId)
            log("layoutId=$layoutId,mipmapId=$drawableId,stringId=$stringId,")
            val imageView: ImageView = layout.kFindViewById(R.id.iv_facing_bottom_tab)
            val textView: TextView = layout.kFindViewById(R.id.tv_facing_bottom_tab)
            imageView.setImageResource(drawableId)
            textView.setText(stringId)
            layout.setOnClickListener(listener)
            layout.setTag(R.id.id_facing_bottom_index, i)
        }
        selectTab(tabIndex)
    }

    private fun selectTab(index: Int) {
        log("selectTab index=$index")
        for (i in 0..3) {
            val layoutId = resources.getIdentifier("ll_facing_bottom_tab_$i", "id", packageName)
            kFindViewById<View>(layoutId).let {
                if (i == index) {
                    it.isSelected = true
                } else if (it.isSelected) {
                    it.isSelected = false
                }
            }
        }
        onBottomItemSelected(index)
    }

    private fun onBottomItemSelected(position: Int) {
        if (currentTabPosition != position) {
            val pFragmentClass = facingFragments[position].first
            val args = createArgsWithTitle(facingFragments[position].second)
            pushFragment(pFragmentClass, args)
            currentTabPosition = position
        }
    }

    override fun onBackPressed() {
        if (currentTabPosition != 0) {
            selectTab(0)
        } else {
            finish()
        }
    }
}
