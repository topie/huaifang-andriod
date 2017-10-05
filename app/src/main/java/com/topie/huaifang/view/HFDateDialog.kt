package com.topie.huaifang.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.topie.huaifang.R
import com.topie.huaifang.util.HFDimensUtils
import kotlinx.android.synthetic.main.view_date_dialog.*
import java.util.*

/**
 * Created by arman on 2017/10/5.
 * 时间选择器
 */
class HFDateDialog(context: Context) : Dialog(context) {

    var onDatePick: ((date: Date) -> Unit)? = null

    var mDate: Date = Date()
        set(value) {
            field = value
            mCalendar.time = mDate
            updateView()
        }

    private val mCalendar: Calendar = Calendar.getInstance().also { it.time = mDate }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_date_dialog)
        setWindowScreenWidth()
        updateView()
        tv_date_dialog_ok.setOnClickListener {
            dismiss()
            onDatePick?.invoke(getPickTime())
        }
        tv_date_dialog_cancel.setOnClickListener {
            dismiss()
        }
        tp_v_date_dialog.setIs24HourView(true)
    }

    private fun getPickTime(): Date {
        mCalendar.set(
                dp_v_date_dialog.year,
                dp_v_date_dialog.month,
                dp_v_date_dialog.dayOfMonth,
                tp_v_date_dialog.currentHour,
                tp_v_date_dialog.currentMinute
        )
        return mCalendar.time
    }

    private fun setWindowScreenWidth() {
        val p = window.attributes
        val display = window.windowManager.defaultDisplay
        p.width = Math.min(display.width, HFDimensUtils.dp2px(320.toFloat()))
        window.attributes = p
    }

    private fun updateView() {
        dp_v_date_dialog.updateDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH))
        tp_v_date_dialog.currentHour = mCalendar.get(Calendar.HOUR_OF_DAY)
        tp_v_date_dialog.currentMinute = mCalendar.get(Calendar.MINUTE)
    }
}