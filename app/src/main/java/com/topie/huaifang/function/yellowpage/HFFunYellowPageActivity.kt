package com.topie.huaifang.function.yellowpage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import com.davdian.ptr.Pt2FrameLayout
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunYellowPageResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.util.HFLogger
import com.topie.huaifang.view.HFTipDialog
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/29.
 * 迷你黄页
 */
class HFFunYellowPageActivity : HFBaseTitleActivity() {

    private var disposable: Disposable? = null

    private lateinit var adapter: Adapter
    private lateinit var pt2FrameLayout: Pt2FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_pt2_list_layout)
        setBaseTitle(R.string.facing_index_fun_yellow_book)
        adapter = Adapter(this)
        pt2FrameLayout = kFindViewById(R.id.pt2_base_pt2)
        pt2FrameLayout.setPt2Handler(HFDefaultPt2Handler { getFunPartyMembersList() })
        val listView: ExpandableListView = pt2FrameLayout.kFindViewById(R.id.elv_base_pt2)
        listView.setAdapter(adapter)
        listView.setOnChildClickListener { _, _, _, _, id ->
            val builder = HFTipDialog.Builder()
            builder.content = "确定拨打：$id?"
            builder.onOkClicked = { this@HFFunYellowPageActivity.kTel(id.toString()) }
            builder.show(this@HFFunYellowPageActivity)
            return@setOnChildClickListener true
        }
    }

    override fun onResume() {
        super.onResume()
        if (adapter.list.isEmpty()) {
            getFunPartyMembersList()
        }
    }

    private fun getFunPartyMembersList() {
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
        disposable = HFRetrofit.hfService.getFunYellowPage().subscribeResultOkApi({
            it.data?.data?.let {
                Group.convertToGroupList(it)
            }?.let {
                adapter.list.clear()
                adapter.list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }, {
            HFLogger.log("complete")
            pt2FrameLayout.complete2()
        })
    }

    override fun onPause() {
        super.onPause()
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
    }

    class Adapter(context: Context) : BaseExpandableListAdapter() {

        val list: MutableList<Group> = arrayListOf()
        val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun getGroup(groupPosition: Int): Group? {
            return list.kGet(groupPosition)
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: inflater.inflate(R.layout.function_party_members_group_title, parent, false)
            val textView = view.kFindViewById<TextView>(R.id.tv_fun_party_members_node)
            textView.text = getGroup(groupPosition)?.typeStr
            return view
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            return getGroup(groupPosition)?.getCount() ?: 0
        }

        override fun getChild(groupPosition: Int, childPosition: Int): HFFunYellowPageResponseBody.ListData? {
            return getGroup(groupPosition)?.getItem(childPosition)
        }

        override fun getGroupId(groupPosition: Int): Long {
            return getGroup(groupPosition)?.typeStr.let { it ?: "NULL" }.hashCode().toLong()
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: inflater.inflate(R.layout.function_yellow_page_list_item, parent, false)
            val tvName = view.kFindViewById<TextView>(R.id.tv_fun_yellow_page_name)
            val tvFlag = view.kFindViewById<TextView>(R.id.tv_fun_yellow_page_phone)
            tvName.text = getChild(groupPosition, childPosition)?.name
            tvFlag.text = getChild(groupPosition, childPosition)?.mobilePhone
            return view
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return getChild(groupPosition, childPosition)?.mobilePhone?.toLongOrNull() ?: 0.toLong()
        }

        override fun getGroupCount(): Int {
            return list.size
        }


    }

    class Group(var typeStr: String? = null) {

        private val list: MutableList<HFFunYellowPageResponseBody.ListData> = arrayListOf()

        companion object {
            fun convertToGroupList(list: List<HFFunYellowPageResponseBody.ListData>): ArrayList<Group> {
                val groupList = ArrayList<Group>()
                list.forEach { item ->
                    groupList.kGetOne(
                            { item.getTypeStr == it.typeStr },
                            { Group(item.getTypeStr) }
                    ).list.add(item)
                }
                return groupList
            }
        }

        fun getCount(): Int {
            return list.size
        }

        fun getItem(position: Int): HFFunYellowPageResponseBody.ListData? {
            return list.kGet(position)
        }
    }
}