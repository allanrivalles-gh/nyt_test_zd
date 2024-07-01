package com.theathletic.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

interface DateChangeListener {
    fun onDateChanged()
}

class DateChangeReceiver(private val dateChangeListener: DateChangeListener) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_DATE_CHANGED) {
            dateChangeListener.onDateChanged()
        }
    }
}