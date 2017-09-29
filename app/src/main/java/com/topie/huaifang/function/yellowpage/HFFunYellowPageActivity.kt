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
import com.topie.huaifang.HFBaseTitleActivity
import com.topie.huaifang.R
import com.topie.huaifang.extensions.HFDefaultPt2Handler
import com.topie.huaifang.extensions.kGet
import com.topie.huaifang.extensions.kGetOne
import com.topie.huaifang.extensions.kTel
import com.topie.huaifang.http.HFServiceDerived
import com.topie.huaifang.http.bean.function.HFFunYellowPageResponseBody
import com.topie.huaifang.http.subscribeApi
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
        pt2FrameLayout = findViewById(R.id.pt2_base_pt2) as Pt2FrameLayout
        pt2FrameLayout.setPt2Handler(HFDefaultPt2Handler { getFunPartyMembersList() })
        val listView: ExpandableListView = pt2FrameLayout.findViewById(R.id.elv_base_pt2) as ExpandableListView
        listView.setAdapter(adapter)
        listView.setOnChildClickListener { _, _, _, _, id ->
            kTel(id.toString())
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
        disposable = HFServiceDerived.getFunYellowPage().subscribeApi({
            it.data?.data?.takeIf { it.isNotEmpty() }?.let {
                Group.convertToGroupList(it)
            }?.let {
                adapter.list.clear()
                adapter.list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }, {
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
            val textView = view.findViewById(R.id.tv_fun_party_members_node) as TextView
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
            val tvName = view.findViewById(R.id.tv_fun_yellow_page_name) as TextView
            val tvFlag = view.findViewById(R.id.tv_fun_yellow_page_phone) as TextView
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