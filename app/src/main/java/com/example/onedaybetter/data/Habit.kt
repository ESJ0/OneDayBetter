package com.example.onedaybetter.data

data class Habit(
    val id: Int,
    val name: String,
    val type: HabitType,
    val description: String = "",
    val weekProgress: List<Boolean> = List(7) { false }
) {
    fun getCompletionPercentage(): Int {
        return (weekProgress.count { it } * 100) / weekProgress.size
    }
}

enum class HabitType {
    EXERCISE,
    SLEEP,
    FOOD,
    VALUE
}

fun HabitType.getIcon(): String {
    return when(this) {
        HabitType.EXERCISE -> "🏃"
        HabitType.SLEEP -> "😴"
        HabitType.FOOD -> "❤️"
        HabitType.VALUE -> "💎"
    }
}