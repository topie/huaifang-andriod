package com.topie.huaifang.facing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.extensions.*
import com.topie.huaifang.function.guide.HFFunGuideActivity
import com.topie.huaifang.function.live.HFFunLiveActivity
import com.topie.huaifang.function.notice.HFFunPublicActivity
import com.topie.huaifang.function.party.HFFunPartyActivity
import com.topie.huaifang.function.yellowpage.HFFunYellowPageActivity
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.communication.HFCommUserInfo
import com.topie.huaifang.http.subscribeApi
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.login.HFLoginActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.facing_index_fragment.*

/**
 * Created by arman on 2017/9/16.
 * app 首页
 */
class HFIndexFragment : HFBaseFragment() {

    private var quesDisposable: Disposable? = null
    private var simFriendDisposable: Disposable? = null

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.facing_index_fragment, container, false)
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
            val list = it.data?.data?.takeIf { it.isNotEmpty() }
            when (list) {
                null -> {
                    mView?.findViewById(R.id.tv_facing_similar_friends_title)?.visibility = View.GONE
                    mView?.findViewById(R.id.ll_facing_similar_friends_content)?.visibility = View.GONE
                }
                else -> {
                    mView?.findViewById(R.id.tv_facing_similar_friends_title)?.visibility = View.VISIBLE
                    mView?.findViewById(R.id.ll_facing_similar_friends_content)?.let {
                        it.visibility = View.VISIBLE
                        var i = 0
                        it.kForeach { v ->
                            val userInfo = list.kGet(i++)
                            when (userInfo) {
                                null -> {
                                    v.visibility = View.INVISIBLE
                                }
                                else -> {
                                    v.visibility = View.VISIBLE
                                    val ivHead = v.findViewById(R.id.iv_facing_friend_head) as? HFImageView
                                    ivHead?.loadImageUri(userInfo.headImage?.kParseUrl())
                                    val tvName = v.findViewById(R.id.tv_facing_friend_name) as? TextView
                                    tvName?.text = userInfo.nickname ?: userInfo.mobilePhone
                                    v.findViewById(R.id.tv_facing_friend_add)?.setOnClickListener {
                                        addFriend(userInfo)
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     *  添加好友
     */
    private fun addFriend(userInfo: HFCommUserInfo) {
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
        quesDisposable = HFRetrofit.hfService.getFunQuestionList().subscribeApi {
            if (!it.resultOk) {
                return@subscribeApi
            }
            val list = it.data?.data?.takeIf { it.size >= 3 }
            when (list) {
                null -> ll_facing_index_questions.visibility = View.GONE
                else -> {
                    ll_facing_index_questions.visibility = View.VISIBLE
                    val obj = ({ v: View, str: String? ->
                        val textView = v.findViewById(R.id.tv_facing_question_desc) as TextView
                        textView.text = str
                    })
                    obj(ll_facing_index_question_0, list[0].name)
                    obj(ll_facing_index_question_1, list[1].name)
                    obj(ll_facing_index_question_2, list[2].name)
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
        val fun0 = view.findViewById(R.id.ll_facing_fun_0)
        (fun0.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_announcement)
        (fun0.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_announcement)
        val fun1 = view.findViewById(R.id.ll_facing_fun_1)
        (fun1.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_guide)
        (fun1.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_guide)
        val fun2 = view.findViewById(R.id.ll_facing_fun_2)
        (fun2.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_bazaar)
        (fun2.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_bazaar)
        val fun3 = view.findViewById(R.id.ll_facing_fun_3)
        (fun3.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_live)
        (fun3.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_live)
        val fun4 = view.findViewById(R.id.ll_facing_fun_4)
        (fun4.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_party)
        (fun4.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_party)
        val fun5 = view.findViewById(R.id.ll_facing_fun_5)
        (fun5.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_yellow_book)
        (fun5.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_yellow_book)

        fun0.setOnClickListener {
            context?.let {
                if (HFAccountManager.isLogin) {
                    startActivity(Intent(it, HFFunPublicActivity::class.java))
                } else {
                    val intent = Intent(it, HFLoginActivity::class.java)
                    intent.putExtra(HFLoginActivity.EXTRA_IS_REGISTER, true)
                    startActivity(intent)
                }
            }
        }

        fun1.setOnClickListener {
            context?.let {
                if (HFAccountManager.isLogin) {
                    val intent = Intent(it, HFFunGuideActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(it, HFLoginActivity::class.java)
                    intent.putExtra(HFLoginActivity.EXTRA_IS_REGISTER, false)
                    startActivity(intent)
                }
            }
        }

        fun3.setOnClickListener {
            context.kStartActivity(HFFunLiveActivity::class.java)
        }

        fun4.setOnClickListener {
            context.kStartActivity(HFFunPartyActivity::class.java)
        }

        fun5.setOnClickListener {
            context.kStartActivity(HFFunYellowPageActivity::class.java)
        }


    }
}