package com.topie.huaifang.function.advice

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseParentViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunAdviceResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.base_pt2_recycler_layout.*
import kotlinx.android.synthetic.main.base_title_layout.*

/**
 * Created by arman on 2017/10/26.
 * 意见箱
 */
class HFFunAdviceActivity : HFBaseTitleActivity() {

    private val mAdapter = Adapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_pt2_recycler_layout)
        setBaseTitle("意见箱")
        setBaseTitleRight("发布")
        tv_base_title_right.setOnClickListener {
            this@HFFunAdviceActivity.kStartActivity(HFFunAdvicePublishActivity::class.java)
        }
        rv_base_pt2.layoutManager = LinearLayoutManager(this)
        rv_base_pt2.adapter = mAdapter

        pt2_base_recycler.setPt2Handler(HFDefaultPt2Handler({
            getData()
        }))
    }


    override fun onResume() {
        super.onResume()
        if (mAdapter.list.isEmpty()) {
            getData()
        }
    }

    private fun getData() {
        HFRetrofit.hfService.getFunAdviceList().subscribeResultOkApi({
            it.data?.data?.kInsteadTo(mAdapter.list)
            mAdapter.notifyDataSetChanged()
        }, {
            pt2_base_recycler?.complete2()
        }).kInto(pauseDisableList)
    }

    private class Adapter(val list: MutableList<HFFunAdviceResponseBody.ListData>) : RecyclerView.Adapter<HFBaseParentViewHolder<HFFunAdviceResponseBody.ListData>>() {

        companion object {
            //未回复的
            const val TYPE_NORMAL = 0
            //回复的
            const val TYPE_HANDLED = 1
        }

        override fun onBindViewHolder(holder: HFBaseParentViewHolder<HFFunAdviceResponseBody.ListData>?, position: Int) {
            holder?.bindData(list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HFBaseParentViewHolder<HFFunAdviceResponseBody.ListData> {
            return when (viewType) {
                TYPE_HANDLED -> HandleItemViewHolder(parent!!)
                else -> ItemViewHolder(parent!!)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (list[position].handleDesc.kIsNotEmpty() && list[position].handlePerson.kIsNotEmpty()) {
                TYPE_HANDLED
            } else {
                TYPE_NORMAL
            }
        }

    }

    private class ItemViewHolder(parent: ViewGroup) : HFBaseParentViewHolder<HFFunAdviceResponseBody.ListData>(parent, R.layout.function_advice_item, true) {

        val tvContactContent: TextView = itemView.kFindViewById(R.id.tv_advice_contact_content)
        val tvContactName: TextView = itemView.kFindViewById(R.id.tv_advice_contact_name)
        val tvContactTime: TextView = itemView.kFindViewById(R.id.tv_advice_contact_time)

        override
        fun onBindData(d: HFFunAdviceResponseBody.ListData) {
            tvContactContent.text = d.messageContent
//            tvContactName.text = d.contactPerson
            tvContactTime.text = d.messageTime
        }
    }

    private class HandleItemViewHolder(parent: ViewGroup) : HFBaseParentViewHolder<HFFunAdviceResponseBody.ListData>(parent, R.layout.function_advice_handle_item, true) {

        val tvContactContent: TextView = itemView.kFindViewById(R.id.tv_advice_contact_content)
        val tvContactName: TextView = itemView.kFindViewById(R.id.tv_advice_contact_name)
        val tvContactTime: TextView = itemView.kFindViewById(R.id.tv_advice_contact_time)

        val tvHandleContent: TextView = itemView.kFindViewById(R.id.tv_advice_handle_content)
        val tvHandleName: TextView = itemView.kFindViewById(R.id.tv_advice_handle_name)
        val tvHandleTime: TextView = itemView.kFindViewById(R.id.tv_advice_handle_time)

        override
        fun onBindData(d: HFFunAdviceResponseBody.ListData) {
            tvContactContent.text = d.messageContent
//            tvContactName.text = d.contactPerson
            tvContactTime.text = d.messageTime
            tvHandleContent.text = d.handleDesc
            tvHandleName.text = d.handlePerson
            tvHandleTime.text = ""
        }
    }
}