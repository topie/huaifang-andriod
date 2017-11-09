package com.topie.huaifang.function.discovery

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import com.topie.huaifang.ImageBrowserActivity
import com.topie.huaifang.R
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.global.RequestCode
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunDisNeighCommRequestBody
import com.topie.huaifang.http.bean.function.HFFunDisNeighborhoodResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.util.HFDimensUtils
import com.topie.huaifang.view.HFImagesLayout
import kotlinx.android.synthetic.main.base_pt2_recycler_layout.*
import kotlinx.android.synthetic.main.base_title_layout.*
import kotlinx.android.synthetic.main.function_dis_neigh_activity.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by arman on 2017/10/11.
 * 邻里圈
 */
class HFFunDisNeighborhoodActivity : HFBaseTitleActivity() {

    private val mAdapter = Adapter()
    private var mCommon: Common? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_dis_neigh_activity)
        setBaseTitle(R.string.facing_discovery_neighborhood)
        setBaseTitleRight("发布")
        tv_base_title_right.setOnClickListener {
            this@HFFunDisNeighborhoodActivity.kStartActivity(HFFunDisNeighborhoodApplyActivity::class.java)
        }
        pt2_base_recycler.setPt2Handler(HFDefaultPt2Handler({ ->
            getNeighborhoodList(1)
        }, { ->
            getNeighborhoodList(mAdapter.mPageSize)
        }))
        rv_base_pt2.layoutManager = LinearLayoutManager(this)
        rv_base_pt2.adapter = mAdapter

        mAdapter.onComment = {
            if (mCommon == null) {
                mCommon = Common(ll_fun_dis_neigh_comm, et_fun_dis_neigh_comm, tv_fun_fun_dis_neigh_send)
            }
            mCommon!!.showComm(it)
        }

        mAdapter.onPraise = { data ->
            val likeData = data.likes?.kGetFirstOrNull { it.userId == HFAccountManager.accountModel.userInfo?.id }
            if (likeData != null) {
                HFRetrofit.hfService.postFunDisUnlike(data.id).subscribeResultOkApi {
                    if (isFinishing) {
                        return@subscribeResultOkApi
                    }
                    data.likes?.remove(likeData)
                    mAdapter.notifyDataSetChanged()
                }.kInto(destroyDisableList)
            } else {
                HFRetrofit.hfService.postFunDisLike(data.id).subscribeResultOkApi {
                    if (isFinishing) {
                        return@subscribeResultOkApi
                    }
                    val ld = HFFunDisNeighborhoodResponseBody.LikeData()
                    ld.likeTime = Date().kToSimpleFormat()
                    ld.userId = HFAccountManager.accountModel.userInfo?.id ?: 0
                    ld.userName = HFAccountManager.accountModel.userInfo?.nickname ?: ""
                    if (data.likes == null) {
                        data.likes = ArrayList()
                    }
                    data.likes!!.add(ld)
                    mAdapter.notifyDataSetChanged()
                }.kInto(destroyDisableList)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.onComment = null
        mAdapter.onPraise = null
    }

    override fun onBackPressed() {
        if (mCommon?.isShown() == true) {
            mCommon?.hideComm()
        } else {
            super.onBackPressed()
        }
    }

    private fun getNeighborhoodList(aPageSize: Int) {
        HFRetrofit.hfService.getFunDisNeighborhoodList(aPageSize).subscribeResultOkApi({
            it.data?.data?.also {
                when (aPageSize) {
                    0, 1 -> mAdapter.setList(it)
                    mAdapter.mPageSize -> mAdapter.addList(it)
                }
                mAdapter.notifyDataSetChanged()
            }
        }, {
            pt2_base_recycler?.complete2()
        }).kInto(pauseDisableList)
    }

    override fun onResume() {
        super.onResume()
        if (mAdapter.itemCount == 0) {
            getNeighborhoodList(1)
        }
    }

    private inner class Common(val layout: View, val et: EditText, send: TextView) {

        var mData: HFFunDisNeighborhoodResponseBody.ListData? = null

        init {
            send.setOnClickListener {
                mData?.id ?: return@setOnClickListener
                val content = et.text.toString().trim().takeIf { it.kIsNotEmpty() } ?: return@setOnClickListener
                val data = mData!!
                val requestBody = HFFunDisNeighCommRequestBody()
                requestBody.id = mData!!.id
                requestBody.content = content
                HFRetrofit.hfService.postFunDisComment(requestBody).subscribeResultOkApi {
                    if (data.comments == null) {
                        data.comments = ArrayList()
                    }
                    val commData = HFFunDisNeighborhoodResponseBody.CommData()
                    commData.commentTime = Date().kToSimpleFormat()
                    commData.content = content
                    commData.userName = HFAccountManager.accountModel.userInfo?.nickname
                    data.comments!!.add(commData)
                    mAdapter.notifyDataSetChanged()
                }.kInto(destroyDisableList)
                hideComm()
            }
        }

        fun hideComm() {
            mData = null
            et.setText("")
            et.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(et.windowToken, 0) //强制隐藏键盘
            layout.visibility = View.GONE
        }

        fun showComm(aData: HFFunDisNeighborhoodResponseBody.ListData) {
            if (aData == mData) {
                return
            }
            mData = aData
            if (layout.visibility != View.VISIBLE) {
                layout.visibility = View.VISIBLE
            }
            et.setText("")
            et.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et, InputMethodManager.SHOW_FORCED)
        }

        fun isShown(): Boolean {
            return layout.visibility != View.GONE
        }
    }

    private class Adapter : RecyclerView.Adapter<ViewHolder>() {

        private val mList: MutableList<HFFunDisNeighborhoodResponseBody.ListData> = arrayListOf()

        var onComment: ((data: HFFunDisNeighborhoodResponseBody.ListData) -> Unit)? = null
        var onPraise: ((data: HFFunDisNeighborhoodResponseBody.ListData) -> Unit)? = null

        var mPageSize = 1
            private set

        fun addList(list: List<HFFunDisNeighborhoodResponseBody.ListData>) {
            mList.addAll(list)
            mPageSize++
        }

        fun setList(list: List<HFFunDisNeighborhoodResponseBody.ListData>) {
            mList.clear()
            mList.addAll(list)
            mPageSize = 2
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindData(mList[position])
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val kInflate = parent.kInflate(R.layout.function_dis_neighborhood_list_item)
            return ViewHolder(kInflate as ViewGroup).apply {
                onComment = { this@Adapter.onComment?.invoke(it) }
                onPraise = { this@Adapter.onPraise?.invoke(it) }
            }
        }

    }

    private class ViewHolder(val mItemView: ViewGroup) : HFBaseRecyclerViewHolder<HFFunDisNeighborhoodResponseBody.ListData>(mItemView) {

        private val mTvName: TextView = itemView.findViewById(R.id.tv_fun_dis_neighborhood_name)
        private val mTvContent: TextView = itemView.findViewById(R.id.tv_fun_dis_neighborhood_content)
        private val mTvTime: TextView = itemView.findViewById(R.id.tv_fun_dis_neighborhood_time)
        //喜欢
        private val mLLLike: View = itemView.findViewById(R.id.ll_fun_dis_neighborhood_like)
        //评论
        private val mLLComm: View = itemView.findViewById(R.id.ll_fun_dis_neighborhood_comm)
        //图片集合
        private val hfImagesLayout: HFImagesLayout = itemView.findViewById(R.id.il_fun_dis_neighborhood_images)
        //喜欢列表
        private val mLLLikeList: LinearLayout = itemView.findViewById(R.id.ll_fun_dis_neighborhood_like_list)
        //评论列表
        private val mLLCommList: LinearLayout = itemView.findViewById(R.id.ll_fun_dis_neighborhood_comm_list)
        var imageList: List<String>? = null

        var onComment: ((data: HFFunDisNeighborhoodResponseBody.ListData) -> Unit)? = null
        var onPraise: ((data: HFFunDisNeighborhoodResponseBody.ListData) -> Unit)? = null

        init {
            hfImagesLayout.setOnItemClickListener(object : HFImagesLayout.OnItemClickListener {
                override fun onAdd() {

                }

                override fun onImageClicked(uri: Uri?, position: Int) {
                    val context = mItemView.context.kFindActivity() ?: mItemView.context
                    val list = imageList ?: arrayListOf()
                    ImageBrowserActivity.openImageBrowserActivity(context, RequestCode.IMAGE_BROWSER, -1, list, null, position)
                }
            })

            mLLLike.setOnClickListener {
                data ?: return@setOnClickListener
                onPraise?.invoke(data!!)
            }

            mLLComm.setOnClickListener {
                data ?: return@setOnClickListener
                onComment?.invoke(data!!)
            }
        }

        override fun onBindData(d: HFFunDisNeighborhoodResponseBody.ListData) {
            bindImages(d.images)
            bindLikeList(d.likes)
            bindCommList(d.comments)
            mTvName.text = d.addUserName
            mTvTime.text = d.publishTime?.kSimpleFormatToDate()?.kSplit() ?: d.publishTime
            mTvContent.text = d.content
        }

        private fun bindCommList(list: List<HFFunDisNeighborhoodResponseBody.CommData>?) {
            //删除旧的View
            val viewList = ArrayList<View>().apply {
                (0 until mLLCommList.childCount)
                        .map { mLLCommList.getChildAt(it) }
                        .forEach { add(it) }
                mLLCommList.removeAllViews()
            }
            val listSize = list?.size?.takeIf { it > 0 } ?: return
            for (i in 0 until listSize) {
                var commItem = viewList.kGet(i)
                if (commItem == null) {
                    commItem = mItemView.kInflate(R.layout.function_dis_neighborhood_comm_item)
                    commItem.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                }
                commItem.findViewById<TextView>(R.id.tv_fun_dis_neighborhood_comm_name)
                        .text = list.kGet(i)?.userName
                commItem.findViewById<TextView>(R.id.tv_fun_dis_neighborhood_comm_content)
                        .text = list.kGet(i)?.content
                mLLCommList.addView(commItem)
            }
        }

        private fun bindLikeList(list: List<HFFunDisNeighborhoodResponseBody.LikeData>?) {
            //删除旧的View
            val viewList = ArrayList<View>()
            (0 until mLLLikeList.childCount).mapTo(viewList) { mLLLikeList.getChildAt(it) }
            mLLLikeList.removeAllViews()
            val listSize = list?.size?.takeIf { it > 0 } ?: return
            for (i in 0 until listSize.coerceAtMost(3)) {
                var praiseItem = viewList.kGet(i)
                if (praiseItem == null) {
                    praiseItem = mItemView.kInflate(R.layout.function_dis_neighborhood_praise_item)
                    val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    layoutParams.rightMargin = when (i) {
                        3 -> 0
                        else -> HFDimensUtils.dp2px(10.toFloat())
                    }
                    praiseItem.layoutParams = layoutParams
                }
                val textView = praiseItem.findViewById<TextView>(R.id.tv_fun_dis_neighborhood_praise_name)
                textView.text = list.kGet(i)?.userName
                mLLLikeList.addView(praiseItem)
            }

        }

        private fun bindImages(images: String?) {
            //获取新数据
            imageList = images
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?.map { HFRetrofit.parseUrlToBase(it) }
            val list = imageList?.filterIndexed { index, _ -> index < 4 }?.map { Uri.parse(it) }
            if (list.kIsEmpty()) {
                hfImagesLayout.visibility = View.GONE
            } else {
                hfImagesLayout.visibility = View.VISIBLE
                hfImagesLayout.setPathList(list)
            }
        }

    }
}