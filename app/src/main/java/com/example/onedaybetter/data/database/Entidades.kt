package com.example.onedaybetter.data.database

import androidx.room.*
import java.time.LocalDate

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val password: String,
    val name: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habits",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["email"],
            childColumns = ["userEmail"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userEmail")]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String,
    val name: String,
    val type: String, // EXERCISE, SLEEP, FOOD, VALUE
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val daysOfWeek: String // "1,2,3,4,5" for Mon-Fri, etc.
)

@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId"), Index("date")]
)
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val habitId: Int,
    val date: String, // Format: "2025-11-15"
    val completed: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["email"],
            childColumns = ["userEmail"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userEmail")]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String,
    val name: String,
    val type: String,
    val description: String = "",
    val targetDate: String,
    val daysOfWeek: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "goal_completions",
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId"), Index("date")]
)
data class GoalCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val goalId: Int,
    val date: String, // Format: "2025-11-15"
    val completed: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)