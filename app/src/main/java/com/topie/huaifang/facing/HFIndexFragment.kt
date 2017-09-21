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
import com.topie.huaifang.function.guide.HFFunGuideActivity
import com.topie.huaifang.login.HFLoginActivity

/**
 * Created by arman on 2017/9/16.
 */
class HFIndexFragment : HFBaseFragment() {

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.facing_index_fragment, container, false)
        initFunctions(inflate, savedInstanceState)
        return inflate
    }

    private fun initFunctions(view: View, savedInstanceState: Bundle?) {
        val fun0 = view.findViewById<View>(R.id.ll_facing_fun_0)
        fun0.findViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_announcement)
        fun0.findViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_announcement)
        val fun1 = view.findViewById<View>(R.id.ll_facing_fun_1)
        fun1.findViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_guide)
        fun1.findViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_guide)
        val fun2 = view.findViewById<View>(R.id.ll_facing_fun_2)
        fun2.findViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_bazaar)
        fun2.findViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_bazaar)
        val fun3 = view.findViewById<View>(R.id.ll_facing_fun_3)
        fun3.findViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_live)
        fun3.findViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_live)
        val fun4 = view.findViewById<View>(R.id.ll_facing_fun_4)
        fun4.findViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(R.mipmap.ic_facing_index_fun_yellow_book)
        fun4.findViewById<TextView>(R.id.tv_facing_index_fun).setText(R.string.facing_index_fun_yellow_book)

        fun0.setOnClickListener {
            context?.let {
                val intent = Intent(it, HFLoginActivity::class.java)
                intent.putExtra(HFLoginActivity.EXTRA_IS_REGISTER, true)
                startActivity(intent)
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
    }
}