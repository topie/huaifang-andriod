package com.topie.huaifang

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by arman on 2017/10/1.
 */
object HFActivityManager {

    private val activityCollections: LinkedList<WeakReference<Activity>> = LinkedList()

    var resumedActivity: WeakReference<Activity>? = null
        private set

    fun isActivityOnAct(clazz: Class<out Activity>): Boolean {
        return activityCollections.firstOrNull { it.get()?.javaClass == clazz }?.get().let {
            when (it) {
                null -> false
                else -> !it.isFinishing
            }
        }
    }

    /**
     * 关闭指定的activity
     */
    fun closeActivity(clazz: Class<out Activity>) {
        activityCollections.forEach {
            it.get()?.takeIf { it.javaClass == clazz }?.finish()
        }
    }

    /**
     * 关闭activity，除了指定的activity
     */
    fun closeActivityIfNot(clazz: Class<out Activity>) {
        activityCollections.forEach {
            it.get()?.takeIf { it.javaClass != clazz }?.finish()
        }
    }

    /**
     * 关闭所有的Activity
     */
    fun closeAllActivities() {
        activityCollections.forEach {
            it.get()?.takeIf {
                !it.isFinishing
            }?.finish()
        }
    }

    object HFActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {


        override fun onActivityPaused(activity: Activity) {
            resumedActivity?.get()?.takeIf { it == activity }?.let { resumedActivity = null }
        }

        override fun onActivityResumed(activity: Activity) {
            resumedActivity = WeakReference(activity)
        }

        override fun onActivityStarted(activity: Activity?) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            activityCollections.firstOrNull { it.get()?.let { it == activity } ?: false }?.let {
                activityCollections.remove(it)
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

        }

        override fun onActivityStopped(activity: Activity?) {

        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activityCollections.add(WeakReference(activity))
        }


    }
}