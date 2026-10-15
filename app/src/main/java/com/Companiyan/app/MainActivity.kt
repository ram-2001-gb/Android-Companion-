package com.companiyan.app

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

private val Purple = Color(0xFF8055D9)
private val Ink = Color(0xFF28223B)

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        setContent { CompaniyanApp() }
    }
}

@Composable
fun CompaniyanApp() {
    val context = LocalContext.current
    var reminders by remember { mutableStateOf(ReminderStore.load(context)) }
    var language by remember { mutableStateOf("Tamil + English") }
    val preferences = remember { context.getSharedPreferences("companiyan_settings", android.content.Context.MODE_PRIVATE) }
    var selectedCharacterId by remember { mutableStateOf(preferences.getString("character_id", "mass_buddy") ?: "mass_buddy") }
    val character = CharacterCatalog.byId(selectedCharacterId)
    var emotion by remember { mutableStateOf("happy") }
    var showAdd by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }
    var chatReply by remember { mutableStateOf("Hey bestie! 😎 Enna plan today? What's the mission?") }
    val transition = rememberInfiniteTransition(label = "buddy-bounce")
    val bounce by transition.animateFloat(1f, 1.06f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "bounce")

    MaterialTheme(colorScheme = lightColorScheme(primary = Purple, background = Color(0xFFFFF8F4), surface = Color.White)) {
        Scaffold(containerColor = Color(0xFFFFF8F4)) { padding ->
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(top = 18.dp, bottom = 36.dp)) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column { Text("Companiyan", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Ink); Text("Your tiny daily sidekick ✨", color = Color.Gray) }
                        Text(character.avatar, fontSize = 32.sp)
                    }
                }
                item {
                    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.background(Brush.linearGradient(listOf(Color(0xFFE8DBFF), Color(0xFFFFE4DB)))).fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            AnimatedContent(targetState = emotion, label = "emotion") { state ->
                                Text(when (state) { "excited" -> "🤩"; "sleepy" -> "😴"; "proud" -> "😎"; "thinking" -> "🤔"; else -> "🥹" }, fontSize = 94.sp, modifier = Modifier.scale(bounce))
                            }
                            Text(character.greeting, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Ink)
                            Text(character.archetype, color = Ink.copy(alpha = 0.75f), modifier = Modifier.padding(top = 4.dp))
                            Text(character.waterReminder, color = Ink, modifier = Modifier.padding(top = 6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 14.dp)) {
                                listOf("happy", "excited", "thinking", "sleepy", "proud").forEach { mood ->
                                    FilledTonalButton(onClick = { emotion = mood }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) { Text(when(mood) {"happy"->"😊";"excited"->"🤩";"thinking"->"🤔";"sleepy"->"😴";else->"😎"}) }
                                }
                            }
                        }
                    }
                }
                item {
                    Text("Choose your original buddy", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Text("Fictional personas — not real celebrity replicas", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 3.dp, bottom = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CharacterCatalog.all.forEach { option ->
                            Card(
                                onClick = {
                                    selectedCharacterId = option.id
                                    preferences.edit().putString("character_id", option.id).apply()
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = if (option.id == selectedCharacterId) Color(0xFFEDE4FF) else Color.White),
                                border = if (option.id == selectedCharacterId) androidx.compose.foundation.BorderStroke(1.dp, Purple) else null
                            ) {
                                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(option.avatar, fontSize = 30.sp, modifier = Modifier.padding(end = 12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(option.name, fontWeight = FontWeight.Bold, color = Ink)
                                        Text(option.archetype, fontSize = 12.sp, color = Color.Gray)
                                    }
                                    if (option.id == selectedCharacterId) Text("✓", color = Purple, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                item { Text("Your daily missions", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { showAdd = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) { Text("＋ Add reminder") }
                        OutlinedButton(onClick = {
                            if (Settings.canDrawOverlays(context)) context.startService(Intent(context, FloatingCharacterService::class.java))
                            else context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
                        }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) { Text("♡ Float buddy") }
                    }
                }
                if (reminders.isEmpty()) item { Text("No reminders yet. Add your first tiny mission! 💧", color = Color.Gray) }
                items(reminders, key = { it.id }) { reminder ->
                    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("⏰", fontSize = 28.sp, modifier = Modifier.padding(end = 12.dp))
                            Column(Modifier.weight(1f)) { Text(reminder.title, fontWeight = FontWeight.Bold, color = Ink); Text("${reminder.timeLabel()} · Daily · ${reminder.language}", color = Color.Gray, fontSize = 12.sp) }
                            IconButton(onClick = {
                                ReminderScheduler.cancel(context, reminder)
                                reminders = reminders.filterNot { it.id == reminder.id }; ReminderStore.save(context, reminders)
                            }) { Text("✕", color = Color.Gray) }
                        }
                    }
                }
                item { Text("Talk to your buddy", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink) }
                item {
                    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(chatReply, color = Ink)
                            OutlinedTextField(value = chatInput, onValueChange = { chatInput = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Tell me something…") }, shape = RoundedCornerShape(14.dp), singleLine = true)
                            Button(onClick = {
                                chatReply = when {
                                    chatInput.isBlank() -> "Pesu bestie! I'm listening. 💜"
                                    chatInput.contains("water", true) || chatInput.contains("தண்ண", true) -> character.waterReminder
                                    chatInput.contains("sad", true) || chatInput.contains("சோகம்", true) -> "Awww 🫂 I'm here with you. One small step at a time, okay?"
                                    chatInput.contains("hi", true) || chatInput.contains("hello", true) -> "Hiiii! 😎 Sollu bestie, what's happening?"
                                    else -> "${character.playfulReply} (Demo chat for now — real AI connection comes later.)"
                                }
                                emotion = if (chatInput.contains("sad", true)) "thinking" else "excited"; chatInput = ""
                            }, modifier = Modifier.fillMaxWidth()) { Text("Chat with me 💬") }
                            Text("Starter chat is offline demo logic. AI responses need a secure API/backend connection.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
                item {
                    Text("Voice language", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Tamil", "English", "Tamil + English").forEach { option ->
                            FilterChip(selected = language == option, onClick = { language = option }, label = { Text(option) })
                        }
                    }
                    Text("Android Text-to-Speech voices vary by device. Install Tamil speech data in your phone settings if needed.", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
    if (showAdd) AddReminderDialog(onDismiss = { showAdd = false }, onSave = { title, hour, minute ->
        val nextId = (reminders.maxOfOrNull { it.id } ?: 0) + 1
        val reminder = Reminder(nextId, title, hour, minute, language)
        reminders = reminders + reminder; ReminderStore.save(context, reminders); ReminderScheduler.schedule(context, reminder); showAdd = false
    })
}

@Composable
private fun AddReminderDialog(onDismiss: () -> Unit, onSave: (String, Int, Int) -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("Drink water 💧") }
    var hour by remember { mutableIntStateOf(10) }
    var minute by remember { mutableIntStateOf(0) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add a daily mission") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Reminder") }, singleLine = true)
            OutlinedButton(onClick = { TimePickerDialog(context, { _, h, m -> hour = h; minute = m }, hour, minute, false).show() }, modifier = Modifier.fillMaxWidth()) { Text("⏰  Time: %02d:%02d".format(hour, minute)) }
            Text("Repeats every day. Your phone may delay reminders due to battery settings.", fontSize = 12.sp, color = Color.Gray)
        }
    }, confirmButton = { Button(onClick = { if (title.isNotBlank()) onSave(title.trim(), hour, minute) }, enabled = title.isNotBlank()) { Text("Save") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
