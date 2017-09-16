package com.topie.huaifang.facing

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kGetIdentifier
import com.topie.huaifang.extensions.log
import me.yokeyword.fragmentation.SupportActivity
import me.yokeyword.fragmentation.SupportFragment

class HFMainActivity : SupportActivity() {

    private var currentTabPosition = -1

    val facingFragments: List<Class<out SupportFragment>> = arrayListOf(
            HFIndexFragment::class.java,
            HFMsgFragment::class.java,
            HFDiscoveryFragment::class.java,
            HFMineFragment::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.facing_main_activity)
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
            val mipmapId = kGetIdentifier("ic_facing_bottom_tab_$i", "mipmap")
            val stringId = kGetIdentifier("facing_bottom_tab_$i", "string")
            val layout = findViewById<View>(layoutId)
            log("layoutId=$layoutId,mipmapId=$mipmapId,stringId=$stringId,")
            if (layout != null) {
                val imageView = layout.findViewById<ImageView>(R.id.iv_facing_bottom_tab)
                val textView = layout.findViewById<TextView>(R.id.tv_facing_bottom_tab)
                imageView.setImageResource(mipmapId)
                textView.setText(stringId)
                layout.setOnClickListener(listener)
                layout.setTag(R.id.id_facing_bottom_index, i)
            }
        }
        selectTab(tabIndex)
    }

    private fun selectTab(index: Int) {
        log("selectTab index=$index")
        for (i in 0..3) {
            val layoutId = resources.getIdentifier("ll_facing_bottom_tab_$i", "id", packageName)
            findViewById<View>(layoutId)?.let {
                if (it.isSelected) {
                    it.isSelected = false
                } else if (i == index) {
                    it.isSelected = true
                }
            }
        }
        onBottomItemSelected(index)
    }

    private fun onBottomItemSelected(position: Int) {
        if (currentTabPosition != position) {
            currentTabPosition = position
            val indexFragment = findFragment(facingFragments[position]) ?: facingFragments[position].newInstance()
            loadRootFragment(R.id.fl_facing_frag, indexFragment, false, true)
        }
    }

    override fun onBackPressedSupport() {
        if (currentTabPosition == 0) {
            super.onBackPressedSupport()
        } else {
            selectTab(0)
        }
    }
}
