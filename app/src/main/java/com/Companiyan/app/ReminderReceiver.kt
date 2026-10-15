package com.companiyan.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("id", 1)
        val title = intent.getStringExtra("title") ?: "Time for a little break!"
        val language = intent.getStringExtra("language") ?: "Tamil + English"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(NotificationChannel("daily_reminders", "Daily reminders", NotificationManager.IMPORTANCE_HIGH))
        val open = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        manager.notify(id, NotificationCompat.Builder(context, "daily_reminders")
            .setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("Companiyan says 💜")
            .setContentText(title).setStyle(NotificationCompat.BigTextStyle().bigText(title))
            .setContentIntent(open).setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH).build())
        val speechIntent = Intent(context, SpeechService::class.java).putExtra("message", title).putExtra("language", language)
        try {
            if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(speechIntent) else context.startService(speechIntent)
        } catch (_: Exception) { /* Notification still works if speech cannot start. */ }
        // Re-arm the next daily occurrence after the alarm fires.
        val saved = ReminderStore.load(context).firstOrNull { it.id == id }
        if (saved != null && saved.enabled) ReminderScheduler.schedule(context, saved)
    }
}
