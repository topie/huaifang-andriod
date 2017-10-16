package com.topie.huaifang.function.other

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.base.HFEmptyRecyclerViewHolder
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunQuestionDetailResponseBody
import com.topie.huaifang.http.bean.function.HFFunQuestionRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.base_title_layout.*

/**
 * Created by arman on 2017/10/2.
 * 问卷调查
 */
class HFQuestionActivity : HFBaseTitleActivity() {

    private val mRequestBody: HFFunQuestionRequestBody = HFFunQuestionRequestBody(-1)
    private var mAdapter: Adapter? = null

    companion object {
        val EXTRA_ID: String = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化请求体，必须在最前面
        initRequestBody(intent)
        val recyclerView = RecyclerView(this)
        setContentView(recyclerView)
        setBaseTitleRight("提交")
        recyclerView.layoutManager = LinearLayoutManager(this)
        tv_base_title_right.setOnClickListener({
            val item = mRequestBody.items.kGetFirstOrNull { it.selectOptionId == -1 }
            if (item != null) {
                kToastShort(item.content?:"还不能提交，问题没有回答完")
                return@setOnClickListener
            }
            HFRetrofit.hfService.postFunQuestions(mRequestBody).subscribeResultOkApi {
                kToastShort("提交成功")
                finish()
            }
        })
        mAdapter = Adapter(mRequestBody)
        recyclerView.adapter = mAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter?.onSubmit = null
        mAdapter = null
    }

    override fun onResume() {
        super.onResume()
        if (mAdapter?.mList?.isNotEmpty() != true) {
            HFRetrofit.hfService.getFunQuestionDetail(mRequestBody.infoId).subscribeResultOkApi {
                it.data?.data?.takeIf { it.isNotEmpty() }?.also {
                    val items = it.map { HFFunQuestionRequestBody.Item(it.questionId,-1,it.question) }
                    mRequestBody.items.kReset(items)
                    val itemList = convertList2ItemList(it)
                    mAdapter?.mList?.kReset(itemList)
                    mAdapter?.notifyDataSetChanged()
                }
            }.kInto(pauseDisableList)
        }
    }

    private fun initRequestBody(intent: Intent?) {
        mRequestBody.infoId = intent?.getIntExtra(EXTRA_ID, 0) ?: 0
    }

    private fun convertList2ItemList(aList: List<HFFunQuestionDetailResponseBody.ListData>): List<Item> {
        val list: MutableList<Item> = arrayListOf()
        aList.forEach {
            val element = Item()
            element.questionId = it.questionId
            element.question = it.question
            element.questionIndex = it.questionIndex
            element.viewType = Item.TYPE_TITLE
            list.add(element)
            it.options?.forEach { opt ->
                val e = Item()
                e.questionId = it.questionId
                e.question = it.question
                e.questionIndex = it.questionIndex

                e.optionId = opt.optionId
                e.optionText = opt.optionText
                e.viewType = Item.TYPE_OPTIONS
                list.add(e)
            }
        }
        return list
    }

    private class Item {

        var questionIndex: Int = -1
        var questionId: Int = -1
        var question: String? = null

        /**
         * 选项id
         */
        var optionId: Int = -1
        var optionText: String? = null

        var viewType: Int = TYPE_NORMAL

        companion object {
            const val TYPE_NORMAL: Int = 0
            const val TYPE_TITLE: Int = 1
            const val TYPE_OPTIONS: Int = 2
            const val TYPE_SUBMIT: Int = 3
        }
    }

    private class OptionViewHolder(val requestBody: HFFunQuestionRequestBody, itemView: View) : HFBaseRecyclerViewHolder<Item>(itemView) {
        val tvPosition: TextView = itemView.kFindViewById(R.id.tv_question_list_item_position)
        val tvTitle: TextView = itemView.kFindViewById(R.id.tv_question_list_item_title)
        val radioButton: RadioButton = itemView.kFindViewById(R.id.rb_question_list_item)

        init {
            tvPosition.visibility = View.INVISIBLE
        }

        override fun onBindData(d: Item) {
            tvTitle.text = d.optionText
            radioButton.isChecked = requestBody.items.firstOrNull {
                it.questionId == d.questionId
            }?.takeIf {
                it.selectOptionId == d.optionId
            }?.let { true } ?: false
            val text = "${d.questionIndex}. "
            tvPosition.text = text
        }

        override fun onItemClicked(d: Item?) {
            super.onItemClicked(d)
            d ?: return
            requestBody.items.firstOrNull {
                it.questionId == d.questionId
            }?.selectOptionId = d.optionId
        }
    }

    private class TitleViewHolder(itemView: View) : HFBaseRecyclerViewHolder<Item>(itemView) {

        val tvPosition: TextView = itemView.kFindViewById(R.id.tv_question_list_item_position)
        val tvTitle: TextView = itemView.kFindViewById(R.id.tv_question_list_item_title)

        override fun onBindData(d: Item) {
            val text = "${d.questionIndex}. "
            tvPosition.text = text
            tvTitle.text = d.question
        }
    }

    private class SubmitViewHolder(itemView: View) : HFEmptyRecyclerViewHolder<Item>(itemView) {

        override fun onBindData(d: Item) {

        }
    }

    private class Adapter(var requestBody: HFFunQuestionRequestBody) : RecyclerView.Adapter<HFBaseRecyclerViewHolder<Item>>() {

        val mList: MutableList<Item> = arrayListOf()
        var onSubmit: ((requestBody: HFFunQuestionRequestBody) -> Unit)? = null

        override fun onBindViewHolder(holder: HFBaseRecyclerViewHolder<Item>, position: Int) {
            holder.bindData(mList[position])
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HFBaseRecyclerViewHolder<Item> {
            return when (viewType) {
                Item.TYPE_TITLE -> {
                    val from = LayoutInflater.from(parent.context)
                    val inflate = from.inflate(R.layout.question_list_item, parent, false)
                    val titleViewHolder = TitleViewHolder(inflate)
                    titleViewHolder
                }
                Item.TYPE_OPTIONS -> {
                    val from = LayoutInflater.from(parent.context)
                    val inflate = from.inflate(R.layout.question_list_item, parent, false)
                    val optionViewHolder = OptionViewHolder(requestBody, inflate)
                    optionViewHolder.radioButton.visibility = View.VISIBLE
                    optionViewHolder.itemView.setOnClickListener {
                        optionViewHolder.onItemClicked(mList[optionViewHolder.adapterPosition])
                        notifyDataSetChanged()
                    }
                    optionViewHolder
                }
                Item.TYPE_SUBMIT -> {
                    val from = LayoutInflater.from(parent.context)
                    val inflate = from.inflate(R.layout.question_list_submit, parent, false)
                    val submitViewHolder = SubmitViewHolder(inflate)
                    submitViewHolder.itemView.setOnClickListener {
                        submitViewHolder.onItemClicked(mList[submitViewHolder.adapterPosition])
                        onSubmit?.invoke(requestBody)
                    }
                    submitViewHolder
                }
                else -> {
                    HFEmptyRecyclerViewHolder(View(parent.context))
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return mList[position].viewType
        }
    }
}