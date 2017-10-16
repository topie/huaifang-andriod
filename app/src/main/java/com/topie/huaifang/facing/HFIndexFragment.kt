package com.topie.huaifang.facing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.extensions.*
import com.topie.huaifang.function.HFFunAllActivity
import com.topie.huaifang.function.guide.HFFunGuideActivity
import com.topie.huaifang.function.live.HFFunLiveActivity
import com.topie.huaifang.function.live.HFFunLiveBazaarActivity
import com.topie.huaifang.function.notice.HFFunNoteDetailActivity
import com.topie.huaifang.function.notice.HFFunPublicActivity
import com.topie.huaifang.function.other.HFQuestionActivity
import com.topie.huaifang.function.party.HFFunPartyActivity
import com.topie.huaifang.function.yellowpage.HFFunYellowPageActivity
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.account.HFUserInfo
import com.topie.huaifang.http.bean.index.HFIndexNewsResponseBody
import com.topie.huaifang.http.subscribeApi
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.view.HFTextViewFlipper
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/16.
 * app 首页
 */
class HFIndexFragment : HFBaseFragment() {

    private var quesDisposable: Disposable? = null
    private var simFriendDisposable: Disposable? = null

    private var mLlQuestion0: LinearLayout? = null
    private var mLlQuestion1: LinearLayout? = null
    private var mLlQuestion2: LinearLayout? = null
    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.facing_index_fragment, container, false)
        mLlQuestion0 = inflate.kFindViewById(R.id.ll_facing_index_question_0)
        mLlQuestion1 = inflate.kFindViewById(R.id.ll_facing_index_question_1)
        mLlQuestion2 = inflate.kFindViewById(R.id.ll_facing_index_question_2)
        initFunctions(inflate, savedInstanceState)
        return inflate
    }

    override fun onResume() {
        super.onResume()
        getFunQuestionList()
        getCommSimilarFriendList()
    }

    /**
     * 可能认识的人
     */
    private fun getCommSimilarFriendList() {
        simFriendDisposable = HFRetrofit.hfService.getCommSimilarFriend().subscribeApi {
            if (!it.resultOk) {
                it.convertMessage().kToastShort()
                return@subscribeApi
            }
            it.data?.data?.takeIf {
                it.isNotEmpty()
            }?.also { list ->
                //数据集合不为空,遍历8个可能认识的人
                val vContent: View = mView?.kFindViewById(R.id.ll_facing_similar_friends_content) ?: return@subscribeApi
                vContent.visibility = View.VISIBLE
                for (i in 0 until 8) {
                    val layoutId = kGetIdentifier("ll_facing_similar_friends_item$i", "id")
                    val layout = vContent.findViewById<View>(layoutId)
                    when {
                        i > list.size - 1 -> layout.visibility = View.GONE
                        else -> {
                            layout.visibility = View.VISIBLE
                            val ivHead: HFImageView = layout.findViewById(R.id.iv_facing_friend_head)
                            ivHead.loadImageUri(list[i].headImage?.kParseUrl())
                            val tvName: TextView = layout.findViewById(R.id.tv_facing_friend_name)
                            tvName.text = list[i].nickname ?: list[i].mobilePhone
                            layout.findViewById<View>(R.id.tv_facing_friend_add).setOnClickListener {
                                addFriend(list[i])
                            }
                        }
                    }
                }
            } ?: let {
                //数据集合为空,隐藏可能认识的人区域
                mView?.kFindViewById<View>(R.id.ll_facing_similar_friends_content)?.visibility = View.GONE
            }
        }
    }

    /**
     *  添加好友
     */
    private fun addFriend(userInfo: HFUserInfo) {
        HFRetrofit.hfService.addCommFriend(userInfo.id).subscribeApi {
            when {
                it.resultOk -> getCommSimilarFriendList()
                else -> it.convertMessage().kToastLong()
            }
        }
    }

    /**
     * 调查问卷
     */
    private fun getFunQuestionList() {
        quesDisposable = HFRetrofit.hfService.getIndexNews().subscribeApi {
            if (!it.resultOk) {
                return@subscribeApi
            }

            val obj = ({ v: View?, data: HFIndexNewsResponseBody.BodyData? ->
                val textView: HFTextViewFlipper? = v?.kFindViewById(R.id.tvf_facing_question_desc)
                //数据集合映射成字符串集合，填充视图
                textView?.setDataList2Start(data?.list?.map { it.title ?: "" }, { index, _ ->
                    when (data?.type) {
                        HFIndexNewsResponseBody.BodyData.TYPE_QUESTION -> { //调查问卷
                            v.kStartActivity(HFQuestionActivity::class.java, Bundle().also {
                                val id = data.list!![index].id
                                it.putInt(HFQuestionActivity.EXTRA_ID, id)
                            })
                        }
                        HFIndexNewsResponseBody.BodyData.TYPE_WU_YE_NOTICE,     //物业公告（通知公告）
                        HFIndexNewsResponseBody.BodyData.TYPE_LIVE_NOTICE -> {  //社区公告（通知公告）
                            v.kStartActivity(HFFunNoteDetailActivity::class.java, Bundle().also {
                                val id = data.list!![index].id
                                it.putInt(HFFunNoteDetailActivity.EXTRA_ID, id)
                            })
                        }
                        else -> log("unknown type [${data?.type}]")
                    }
                })
            })

            it.data.also {
                it?.kGetFirstOrNull {
                    it.type == HFIndexNewsResponseBody.BodyData.TYPE_QUESTION
                }.let {
                    obj.invoke(mLlQuestion0, it)
                }
            }.also {
                it?.kGetFirstOrNull {
                    it.type == HFIndexNewsResponseBody.BodyData.TYPE_LIVE_NOTICE
                }.let {
                    obj.invoke(mLlQuestion1, it)
                }
            }.also {
                it?.kGetFirstOrNull {
                    it.type == HFIndexNewsResponseBody.BodyData.TYPE_WU_YE_NOTICE
                }.let {
                    obj.invoke(mLlQuestion2, it)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        quesDisposable?.takeIf { !it.isDisposed }?.dispose()
        simFriendDisposable?.takeIf { !it.isDisposed }?.dispose()
    }

    private fun initFunctions(view: View, @Suppress("UNUSED_PARAMETER") savedInstanceState: Bundle?) {
        val fun0 = view.kFindViewById<View>(R.id.ll_facing_fun_0)
        fun0.kFindViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_announcement)
        fun0.kFindViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_announcement)
        val fun1 = view.kFindViewById<View>(R.id.ll_facing_fun_1)
        fun1.kFindViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_guide)
        fun1.kFindViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_guide)
        val fun2 = view.kFindViewById<View>(R.id.ll_facing_fun_2)
        fun2.kFindViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_bazaar)
        fun2.kFindViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_bazaar)
        val fun3 = view.kFindViewById<View>(R.id.ll_facing_fun_3)
        fun3.kFindViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_live)
        fun3.kFindViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_live)
        val fun4 = view.kFindViewById<View>(R.id.ll_facing_fun_4)
        fun4.kFindViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_party)
        fun4.kFindViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_party)
        val fun5 = view.kFindViewById<View>(R.id.ll_facing_fun_5)
        fun5.kFindViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_yellow_book)
        fun5.kFindViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_yellow_book)

        val fun6 = view.kFindViewById<View>(R.id.ll_facing_fun_6)
        fun6.kFindViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_all)
        fun6.kFindViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.default_all)

        //通知公告
        fun0.setOnClickListener {
            this@HFIndexFragment.kStartActivity(HFFunPublicActivity::class.java)
        }
        //办事指南
        fun1.setOnClickListener {
            this@HFIndexFragment.kStartActivity(HFFunGuideActivity::class.java)
        }
        //邻里圈
        fun2.setOnClickListener {
            this@HFIndexFragment.kStartActivity(HFFunLiveBazaarActivity::class.java)
        }

        fun3.setOnClickListener {
            this@HFIndexFragment.kStartActivity(HFFunLiveActivity::class.java)
        }

        fun4.setOnClickListener {
            this@HFIndexFragment.kStartActivity(HFFunPartyActivity::class.java)
        }

        fun5.setOnClickListener {
            this@HFIndexFragment.kStartActivity(HFFunYellowPageActivity::class.java)
        }

        fun6.setOnClickListener {
            this@HFIndexFragment.kStartActivity(HFFunAllActivity::class.java)
        }
    }
}