package com.topie.huaifang.base

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kGetString
import com.topie.huaifang.extensions.kReleaseSelf
import com.topie.huaifang.extensions.kRemoveChildsWithout

/**
 * Created by arman on 2017/9/20.
 * 带有标题的activity，和系统一个用法
 */
abstract class HFBaseTitleActivity : HFBaseActivity() {
    private var mRootView: ViewGroup? = null

    companion object {
        const val ARG_TITLE = "HFBaseTitleActivity.title"
    }

    override fun pushFragment(pFragmentClass: Class<out Fragment>, args: Bundle?) {
        super.pushFragment(pFragmentClass, args)
        curFragment?.arguments?.getString(ARG_TITLE)?.let {
            setBaseTitle(it)
        }
    }

    fun createArgsWithTitle(title: String): Bundle {
        val bundle = Bundle()
        bundle.putString(ARG_TITLE, title)
        return bundle
    }

    fun createArgsWithTitle(@StringRes strId: Int): Bundle {
        val bundle = Bundle()
        bundle.putString(ARG_TITLE, kGetString(strId))
        return bundle
    }

    override fun setContentView(layoutResID: Int) {
        checkRootView()
        val viewGroup: ViewGroup = mRootView!!.kFindViewById(R.id.fl_base_title_root)
        val inflate = LayoutInflater.from(this)
                .inflate(layoutResID, viewGroup, false)
        setContentView(inflate)
    }

    override fun setContentView(view: View?) {
        checkRootView()
        view?.let {
            val container: View = mRootView!!.kFindViewById(R.id.fl_base_title_container)
            val height = container.layoutParams.height.let { Math.max(0, it) }
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            lp.topMargin = height
            mRootView!!.addView(it, lp)
        }
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        checkRootView()
        view?.let {
            mRootView!!.addView(it, params)
        }
    }

    @Suppress("MemberVisibilityCanPrivate")
    fun setContentViewSuper(view: View?) {
        super.setContentView(view)
    }

    private fun checkRootView() {
        if (mRootView == null) {
            mRootView = LayoutInflater.from(this)
                    .inflate(
                            R.layout.base_title_layout,
                            kFindViewById(android.R.id.content),
                            false
                    ) as ViewGroup?
            mRootView!!.kFindViewById<View>(R.id.iv_base_title_back).setOnClickListener {
                onBackPressed()
            }
        }
        mRootView!!.kReleaseSelf()
        mRootView!!.kRemoveChildsWithout(R.id.fl_base_title_container)
        setContentViewSuper(mRootView)
    }

    fun setBaseTitle(id: Int) {
        val textView: TextView = mRootView!!.kFindViewById(R.id.tv_base_title)
        textView.setText(id)
    }

    fun setBaseTitle(title: String?) {
        val textView: TextView = mRootView!!.kFindViewById(R.id.tv_base_title)
        textView.text = title
    }

    fun setBaseTitleRight(id: Int) {
        val textView: TextView = mRootView!!.kFindViewById(R.id.tv_base_title_right)
        textView.setText(id)
    }

    fun setBaseTitleRight(title: String) {
        val textView: TextView = mRootView!!.kFindViewById(R.id.tv_base_title_right)
        textView.text = title
    }
}