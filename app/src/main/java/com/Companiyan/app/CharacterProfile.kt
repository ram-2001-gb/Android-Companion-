package com.companiyan.app

/** Original fictional personas. Do not model these on identifiable real people. */
data class CharacterProfile(
    val id: String,
    val name: String,
    val archetype: String,
    val avatar: String,
    val greeting: String,
    val waterReminder: String,
    val workoutReminder: String,
    val playfulReply: String
)

object CharacterCatalog {
    val all = listOf(
        CharacterProfile(
            id = "mass_buddy",
            name = "Mass Buddy",
            archetype = "Energetic & dramatic",
            avatar = "😎",
            greeting = "Vanakkam, thalaiva! Innaiku namma day-a mass-a maathuvom!",
            waterReminder = "Thalaiva, water break! Oru glass kudichitu comeback kudu! 💧",
            workoutReminder = "Get up, champ! Workout-ku intro music ready! 💪",
            playfulReply = "Mass-u! Small steps, big hero entry. Enna next mission?"
        ),
        CharacterProfile(
            id = "cheerful_star",
            name = "Cheerful Star",
            archetype = "Warm & encouraging",
            avatar = "🌟",
            greeting = "Hey bestie! Saptiya? Innaiku unakku nalla day-a irukkattum! 💛",
            waterReminder = "Hey bestie, konjam water kudichikko. Unna nalla paathukkanum! 💧",
            workoutReminder = "You can do this! Konjam movement, neraya good vibes! ✨",
            playfulReply = "Awww, naan un team-la irukken! One step at a time. 💖"
        ),
        CharacterProfile(
            id = "creator_buddy",
            name = "Creator Buddy",
            archetype = "Quirky & meme-loving",
            avatar = "🎧",
            greeting = "Yo creator! Innaiku content vida un goals-ku first priority! 😂",
            waterReminder = "Breaking news: un body-ku water venum! Hydration challenge accepted-aa? 💧",
            workoutReminder = "POV: nee workout mudichitta, future-you proud-a iruppa! 🔥",
            playfulReply = "Plot twist: namma productivity arc start aagiduchu! 😂"
        )
    )

    fun byId(id: String?): CharacterProfile = all.firstOrNull { it.id == id } ?: all.first()
}
