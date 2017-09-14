package com.topie.huaifang

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.topie.huaifang.extensions.kGetIdentifier
import com.topie.huaifang.extensions.kGetResourceEntryName
import com.topie.huaifang.extensions.log
import com.topie.huaifang.http.*
import com.topie.huaifang.http.bean.LoginRequestBody
import com.topie.huaifang.imageloader.GlideApp
import me.yokeyword.fragmentation.SupportActivity

class MainActivity : SupportActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        initBottomView(savedInstanceState)
    }

    private fun initBottomView(savedInstanceState: Bundle?) {
        val listener: View.OnClickListener = View.OnClickListener {
            val index = kGetResourceEntryName(it.id)?.split("_")?.lastOrNull()?.toIntOrNull()
            index ?: return@OnClickListener
            selectTab(index)
        }
        val tabIndex = savedInstanceState?.getInt("index_bottom_tab_index") ?: 1
        for (i in 1..4) {
            val layoutId = kGetIdentifier("ll_index_bottom_tab_$i", "id")
            val mipmapId = kGetIdentifier("ic_index_bottom_tab_$i", "mipmap")
            val stringId = kGetIdentifier("index_bottom_tab_$i", "string")
            val layout = findViewById<View>(layoutId)
            log("layoutId=$layoutId,mipmapId=$mipmapId,stringId=$stringId,")
            if (layout != null) {
                val imageView = layout.findViewById<ImageView>(R.id.iv_index_bottom_tab)
                val textView = layout.findViewById<TextView>(R.id.tv_index_bottom_tab)
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
            findViewById<View>(layoutId)?.isSelected = (i == index)
        }
        val login = HFRetrofit.hfService.login(LoginRequestBody("admin", "admin"))
        login.composeApi().subscribe({}, {}, {})
    }
}
