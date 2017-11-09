package com.topie.huaifang.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.topie.huaifang.extensions.kClone
import com.topie.huaifang.extensions.log
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/10/2.
 *
 */
abstract class HFBaseActivity : AppCompatActivity() {

    val pauseDisableList: MutableList<Disposable> = arrayListOf()
    val destroyDisableList: MutableList<Disposable> = arrayListOf()

    var curFragment: Fragment? = null
        private set
    @IdRes
    var mContentId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (destroyDisableList.isNotEmpty()) {
            val kClone = destroyDisableList.kClone()
            destroyDisableList.clear()
            kClone.forEach {
                if (!it.isDisposed) {
                    it.dispose()
                }
            }
            kClone.clear()
        }
    }

    override fun finish() {
        super.finish()
        if (destroyDisableList.isNotEmpty()) {
            val kClone = destroyDisableList.kClone()
            destroyDisableList.clear()
            kClone.forEach {
                if (!it.isDisposed) {
                    it.dispose()
                }
            }
            kClone.clear()
        }
    }

    override fun onPause() {
        super.onPause()
        if (pauseDisableList.isNotEmpty()) {
            val kClone = pauseDisableList.kClone()
            pauseDisableList.clear()
            kClone.forEach {
                if (!it.isDisposed) {
                    it.dispose()
                }
            }
            kClone.clear()
        }
    }

    /**
     * 推送一个fragment进入堆栈，如果需要推入的fragment已经存在，移除顶部的fragment
     * 效果类似于activity的SingleTop
     *
     * @param pFragmentClass
     */
    open fun pushFragment(pFragmentClass: Class<out Fragment>, args: Bundle? = null) {
        try {
            val name = pFragmentClass.name
            val manager = supportFragmentManager

            val ft = manager.beginTransaction()
            curFragment = manager.findFragmentByTag(name)?.also {
                if (it != curFragment) {
                    if (args != null) {
                        try {
                            it.arguments = args
                        } catch (e: Exception) {
                            //do nothing
                        }
                    }
                    ft.attach(it)
                    if (curFragment != null) {
                        ft.detach(curFragment)
                    }
                }
            } ?: pFragmentClass.newInstance().also {
                if (args != null) {
                    try {
                        it.arguments = args
                    } catch (e: Exception) {
                        //do nothing
                    }
                }
                ft.add(mContentId, it, name)
                if (curFragment != null) {
                    ft.detach(curFragment)
                }
            }
            ft.commitAllowingStateLoss()
        } catch (pE: Exception) {
            log("", pE)
        }
    }
}