package com.topie.huaifang.function.communication

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.HFDefaultPt2Handler
import com.topie.huaifang.extensions.kGetColor
import com.topie.huaifang.http.bean.communication.HFCommMsgDetail
import com.topie.huaifang.imageloader.HFImageView
import kotlinx.android.synthetic.main.function_comm_chart_activity.*

/**
 * Created by arman on 2017/10/11.
 * 聊天室
 */
class HFCommChatActivity : HFBaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_comm_chart_activity)
        setBaseTitle("聊天室")
        pt2_fun_chart.setPt2Handler(HFDefaultPt2Handler())
        pt2_fun_chart.setBackgroundColor(kGetColor(R.color.facing_index_background))
    }

    private class LeftViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFCommMsgDetail>(itemView, true) {

        private val mIvHead: HFImageView = itemView.findViewById(R.id.iv_fun_com_msg_head)
        private val mTvMsg: TextView = itemView.findViewById(R.id.tv_fun_comm_content)
        private val mTvHead: TextView = itemView.findViewById(R.id.tv_fun_comm_name)

        override fun onBindData(d: HFCommMsgDetail) {
            
        }
    }
}