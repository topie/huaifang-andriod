package com.topie.huaifang.function.communication

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.base.HFBaseParentViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.im.IMApiLooperService
import com.topie.huaifang.im.IMInterface
import com.topie.huaifang.im.IMMessage
import com.topie.huaifang.imageloader.HFImageView
import kotlinx.android.synthetic.main.function_comm_chart_activity.*

/**
 * Created by arman on 2017/10/11.
 * 聊天室
 */
class HFCommChatActivity : HFBaseTitleActivity(), IMInterface.MessageReceiver {
    private var linearLayoutManager: LinearLayoutManager? = null
    private val adapter = Adapter(mutableListOf())
    private var imInterface: IMInterface? = null
    private var mConnectUserId = 0

    companion object {

        const val EXTRA_CONNECT_USER_ID = "EXTRA_CONNECT_USER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mConnectUserId = intent.getIntExtra(EXTRA_CONNECT_USER_ID, mConnectUserId)
        setContentView(R.layout.function_comm_chart_activity)
        setBaseTitle("聊天室")
        linearLayoutManager = LinearLayoutManager(this)
        rv_fun_chart.layoutManager = linearLayoutManager
        rv_fun_chart.adapter = adapter
        tv_fun_chart_send.setOnClickListener {
            val msg = et_fun_chart.text.toString().takeIf { it.trim().isNotEmpty() } ?: return@setOnClickListener
            et_fun_chart.setText("")
            val imMessage = IMMessage()
            imMessage.userId = HFAccountManager.accountModel.userInfo?.id ?: 0
            imMessage.userName = HFAccountManager.accountModel.userInfo?.nickname
            imMessage.headImage = HFAccountManager.accountModel.userInfo?.headImage.kParseUrl()
            imMessage.content = msg
            imMessage.sendTime = System.currentTimeMillis() / 1000
            imInterface?.sendMessage(imMessage, object : IMInterface.SendMessageCallback {
                override fun callback(imMessage: IMMessage, result: Boolean) {
                    if (result) {
                        adapter.list.add(imMessage)
                        adapter.notifyDataSetChanged()
                        val lastPosition = adapter.itemCount - 1
                        if (lastPosition >= 0) {
                            rv_fun_chart.smoothScrollToPosition(lastPosition)
                        }
                    } else {
                        kToastShort("发送失败")
                    }
                }
            })
        }
        imInterface = IMApiLooperService(mConnectUserId)
        imInterface!!.registerMessageReceiver(this@HFCommChatActivity)
    }

    override fun finish() {
        super.finish()
        disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
    }

    private fun disconnect() {
        imInterface?.unregisterMessageReceiver(this)
    }

    override fun onReceive(list: List<IMMessage>) {
        rv_fun_chart ?: return
        val manager = linearLayoutManager ?: return
        val lastVisibleItemPosition = manager.findLastVisibleItemPosition()
        val isLast = lastVisibleItemPosition == adapter.itemCount - 1
        adapter.list.addAll(list)
        adapter.notifyDataSetChanged()
        if (isLast) {
            rv_fun_chart.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }

    private class Adapter(val list: MutableList<IMMessage>) : RecyclerView.Adapter<HFBaseParentViewHolder<IMMessage>>() {

        companion object {
            private const val TYPE_LEFT = 1
            private const val TYPE_RIGHT = 2
        }

        override fun onBindViewHolder(holder: HFBaseParentViewHolder<IMMessage>?, position: Int) {
            holder?.bindData(list[position])
        }

        override fun getItemCount(): Int {
            return list.count()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HFBaseParentViewHolder<IMMessage> {
            return when (viewType) {
                TYPE_RIGHT -> RightViewHolder(parent!!)
                else -> LeftViewHolder(parent!!)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when {
                list[position].userId == HFAccountManager.accountModel.userInfo?.id -> TYPE_RIGHT
                else -> TYPE_LEFT
            }
        }

    }

    private class LeftViewHolder(parent: ViewGroup) : HFBaseParentViewHolder<IMMessage>(parent, R.layout.function_comm_msg_list_item_left, true) {

        private val mIvHead: HFImageView = itemView.findViewById(R.id.iv_fun_com_msg_head)
        private val mTvMsg: TextView = itemView.findViewById(R.id.tv_fun_comm_content)
        private val mTvName: TextView = itemView.findViewById(R.id.tv_fun_comm_name)

        override fun onBindData(d: IMMessage) {
            mIvHead.loadImageUri(d.headImage)
            mTvMsg.text = d.content
            mTvName.text = d.userName
        }
    }

    private class RightViewHolder(parent: ViewGroup) : HFBaseParentViewHolder<IMMessage>(parent, R.layout.function_comm_msg_list_item_right, true) {

        private val mIvHead: HFImageView = itemView.findViewById(R.id.iv_fun_com_msg_head)
        private val mTvMsg: TextView = itemView.findViewById(R.id.tv_fun_comm_content)
        private val mTvName: TextView = itemView.findViewById(R.id.tv_fun_comm_name)

        override fun onBindData(d: IMMessage) {
            mIvHead.loadImageUri(d.headImage)
            mTvMsg.text = d.content
            mTvName.text = d.userName
        }
    }
}