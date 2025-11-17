package com.example.onedaybetter.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Home

@Serializable
object HabitsList

@Serializable
object AddHabit

@Serializable
data class HabitDetail(val habitId: Int)

@Serializable
object GoalsList

@Serializable
object AddGoal

@Serializable
data class GoalDetail(val goalId: Int)

@Serializable
object Profile