package com.companiyan.app

data class Reminder(
    val id: Int,
    val title: String,
    val hour: Int,
    val minute: Int,
    val language: String = "Tamil + English",
    val enabled: Boolean = true
) {
    fun timeLabel(): String = "%02d:%02d".format(hour, minute)
}
