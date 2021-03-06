package com.topie.huaifang.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topie.huaifang.extensions.kClone
import com.topie.huaifang.extensions.kReleaseSelf
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/17.
 */
abstract class HFBaseFragment : Fragment() {

    val pauseDisableList: MutableList<Disposable> = arrayListOf()

    protected var mView: View? = null

    override final fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = mView?.let {
            it.kReleaseSelf()
            return@let it
        } ?: let {
            return@let onCreateViewSupport(inflater, container, savedInstanceState)
        }
        return mView
    }

    abstract fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

    override fun onPause() {
        super.onPause()
        val kClone = pauseDisableList.kClone()
        pauseDisableList.clear()
        kClone.forEach {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        kClone.clear()
    }
    
    open fun onBackPressed() {

    }
}