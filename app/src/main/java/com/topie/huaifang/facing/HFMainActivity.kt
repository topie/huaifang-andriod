package com.topie.huaifang.facing

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kGetIdentifier
import com.topie.huaifang.extensions.log
import com.topie.huaifang.login.HFLoginActivity


class HFMainActivity : AppCompatActivity() {

    private var currentTabPosition = -1

    private val facingFragments: List<Class<out Fragment>> = arrayListOf(
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
            val drawableId = kGetIdentifier("ic_facing_bottom_tab_$i", "drawable")
            val stringId = kGetIdentifier("facing_bottom_tab_$i", "string")
            val layout = findViewById<View>(layoutId)
            log("layoutId=$layoutId,mipmapId=$drawableId,stringId=$stringId,")
            if (layout != null) {
                val imageView = layout.findViewById<ImageView>(R.id.iv_facing_bottom_tab)
                val textView = layout.findViewById<TextView>(R.id.tv_facing_bottom_tab)
                imageView.setImageResource(drawableId)
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
                if (i == index) {
                    it.isSelected = true
                } else if (it.isSelected) {
                    it.isSelected = false
                }
            }
        }
        onBottomItemSelected(index)
    }

    private var curFragment: Fragment? = null

    /**
     * 推送一个fragment进入堆栈，如果需要推入的fragment已经存在，移除顶部的fragment
     * 效果类似于activity的SingleTop
     *
     * @param pFragmentClass
     */
    private fun pushFragment(pFragmentClass: Class<out Fragment>) = try {
        val name = pFragmentClass.name
        val manager = supportFragmentManager
        val fragment = manager.findFragmentByTag(name) ?: pFragmentClass.newInstance()
        val ft = manager.beginTransaction()
        when (curFragment) {
            null -> log("curFragment is null")
            fragment -> log("fragment[$name] is showing")
            else -> ft.hide(curFragment)
        }
        when {
            fragment.isAdded -> ft.show(fragment)
            else -> ft.add(R.id.fl_facing_frag, fragment, name)
        }
        ft.commitAllowingStateLoss()
        curFragment = fragment
    } catch (pE: Exception) {
        log("", pE)
    }

    private fun onBottomItemSelected(position: Int) {
        if (currentTabPosition != position) {
            pushFragment(facingFragments[position])
            currentTabPosition = position
        }
    }

    override fun onBackPressed() {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount > 1) {
            selectTab(0)
        } else {
            finish()
        }
    }
}
