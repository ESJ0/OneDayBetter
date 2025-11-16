package com.example.onedaybetter.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

fun HabitType.getIcon(): String {
    return when(this) {
        HabitType.EXERCISE -> "🏃"
        HabitType.SLEEP -> "😴"
        HabitType.FOOD -> "❤️"
        HabitType.VALUE -> "💎"
    }
}