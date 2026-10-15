package com.companiyan.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object ReminderStore {
    private const val PREFS = "companiyan_prefs"
    private const val KEY = "reminders"
    fun load(context: Context): List<Reminder> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Reminder(o.getInt("id"), o.getString("title"), o.getInt("hour"), o.getInt("minute"), o.optString("language", "Tamil + English"), o.optBoolean("enabled", true))
            }
        } catch (_: Exception) { emptyList() }
    }
    fun save(context: Context, reminders: List<Reminder>) {
        val arr = JSONArray()
        reminders.forEach { r -> arr.put(JSONObject().put("id", r.id).put("title", r.title).put("hour", r.hour).put("minute", r.minute).put("language", r.language).put("enabled", r.enabled)) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply()
    }
}
