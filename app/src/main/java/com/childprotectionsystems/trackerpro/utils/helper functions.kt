package com.childprotectionsystems.trackerpro.utils

import androidx.compose.ui.graphics.Color

private val initialsColors = listOf(
    Color(0xFFB39DDB), // Purple
    Color(0xFF80CBC4), // Teal
    Color(0xFFFFAB91), // Orange
    Color(0xFFFFF59D), // Yellow
    Color(0xFFA5D6A7), // Green
    Color(0xFF90CAF9), // Blue
    Color(0xFFE57373), // Red
    Color(0xFFCE93D8), // Lavender
    Color(0xFFFFF176), // Light Yellow
    Color(0xFF81D4FA)  // Light Blue
)

/**
 * Returns initials for a given name (max 2 letters).
 */
fun getInitials(name: String): String {
    val words = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        words.isEmpty() -> ""
        words.size == 1 -> words[0].take(2).uppercase()
        else -> (words[0].take(1) + words[1].take(1)).uppercase()
    }
}

/**
 * Returns a color from initialsColors based on the hash of the name.
 */
fun getColorForName(name: String): Color {
    if (name.isBlank()) return initialsColors[0]
    val idx = Math.abs(name.hashCode()) % initialsColors.size
    return initialsColors[idx]
}