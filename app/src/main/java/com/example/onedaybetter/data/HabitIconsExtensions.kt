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

fun HabitType.getIconVector(): ImageVector {
    return when(this) {
        HabitType.EXERCISE -> Icons.Default.DirectionsRun
        HabitType.SLEEP -> Icons.Default.Bedtime
        HabitType.FOOD -> Icons.Default.Restaurant
        HabitType.VALUE -> Icons.Default.Star
    }
}

fun HabitType.getDisplayName(): String {
    return when(this) {
        HabitType.EXERCISE -> "Ejercicio"
        HabitType.SLEEP -> "Sueño"
        HabitType.FOOD -> "Alimentación"
        HabitType.VALUE -> "Valor"
    }
}