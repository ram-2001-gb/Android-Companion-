package com.companiyan.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Restores daily alarms after reboot, clock changes, time-zone changes, or app replacement. */
class ReminderRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED ->
                ReminderScheduler.scheduleAll(context, ReminderStore.load(context))
        }
    }
}
