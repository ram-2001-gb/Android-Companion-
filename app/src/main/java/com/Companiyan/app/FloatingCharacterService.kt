package com.companiyan.app

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface
import android.view.WindowManager.LayoutParams

class FloatingCharacterService : Service() {
    private var windowManager: WindowManager? = null
    private var character: TextView? = null
    override fun onCreate() {
        super.onCreate()
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        character = TextView(this).apply {
            text = "🥹\n💜"; textSize = 34f; gravity = Gravity.CENTER
            setTextColor(Color.WHITE); setBackgroundColor(Color.TRANSPARENT); typeface = Typeface.DEFAULT_BOLD
            setOnClickListener { text = if (text.toString().startsWith("🥹")) "😎\n✨" else "🥹\n💜" }
        }
        val type = if (Build.VERSION.SDK_INT >= 26) LayoutParams.TYPE_APPLICATION_OVERLAY else LayoutParams.TYPE_PHONE
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, type, LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.END; x = 24; y = 220 }
        try { windowManager?.addView(character, params) } catch (_: Exception) { stopSelf() }
    }
    override fun onDestroy() { try { if (character?.parent != null) windowManager?.removeView(character) } catch (_: Exception) {}; character = null; super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null
}
