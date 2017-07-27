package com.topie.huaifang

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.topie.huaifang.extensions.log
import me.yokeyword.fragmentation.SupportActivity

class MainActivity : SupportActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        initBottomView(savedInstanceState)
    }

    private fun initBottomView(savedInstanceState: Bundle?) {

        val listener: View.OnClickListener = View.OnClickListener {
            val index = resources
                    .getResourceEntryName(it.id)
                    ?.split("_")
                    ?.takeIf { !it.isEmpty() }
                    ?.let { return@let it[it.size - 1] }
                    ?.let { return@let it.toIntOrNull() }
            index ?: return@OnClickListener
            selectTab(index)
        }
        val tabIndex = savedInstanceState?.getInt("index_bottom_tab_index") ?: 1
        for (i in 1..4) {
            val layoutId = resources.getIdentifier("ll_index_bottom_tab_$i", "id", packageName)
            val mipmapId = resources.getIdentifier("ic_index_bottom_tab_$i", "mipmap", packageName)
            val stringId = resources.getIdentifier("index_bottom_tab_$i", "string", packageName)
            val layout = findViewById(layoutId)
            log("layoutId=$layoutId,mipmapId=$mipmapId,stringId=$stringId,")
            if (layout != null) {
                val imageView = layout.findViewById(R.id.iv_index_bottom_tab) as ImageView
                val textView = layout.findViewById(R.id.tv_index_bottom_tab) as TextView
                imageView.setImageResource(mipmapId)
                textView.setText(stringId)
                layout.setOnClickListener(listener)
                if (i == tabIndex) {
                    layout.isSelected = true
                }
            }
        }
    }

    private fun selectTab(index: Int) {
        log("selectTab index=$index")
        for (i in 1..4) {
            val layoutId = resources.getIdentifier("ll_index_bottom_tab_$i", "id", packageName)
            findViewById(layoutId)?.isSelected = (i == index)
        }
    }
}
