package com.companiyan.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import java.util.Locale

class SpeechService : Service(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var message: String = ""
    private var language: String = "Tamil + English"
    private var currentStartId: Int = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentStartId = startId
        message = intent?.getStringExtra("message") ?: "Time for a little break!"
        language = intent?.getStringExtra("language") ?: "Tamil + English"
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(NotificationChannel("speech_service", "Companiyan voice", NotificationManager.IMPORTANCE_LOW))
        }
        startForeground(4201, NotificationCompat.Builder(this, "speech_service")
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setContentTitle("Companiyan is speaking")
            .setContentText(message).setOngoing(true).build())
        if (tts == null) tts = TextToSpeech(this, this) else speak()
        return START_NOT_STICKY
    }

    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) speak() }

    private fun speak() {
        val locale = if (language == "Tamil") Locale("ta", "IN") else Locale("en", "IN")
        val engine = tts ?: run { stopSelf(currentStartId); return }
        val result = engine.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            engine.setLanguage(Locale.getDefault())
        }
        engine.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) { stopSelf(currentStartId) }
            @Deprecated("Deprecated in Java") override fun onError(utteranceId: String?) { stopSelf(currentStartId) }
            override fun onError(utteranceId: String?, errorCode: Int) { stopSelf(currentStartId) }
        })
        engine.speak(message, TextToSpeech.QUEUE_FLUSH, null, "companiyan-$currentStartId")
    }

    override fun onDestroy() { tts?.stop(); tts?.shutdown(); tts = null; super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null
}
