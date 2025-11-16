package com.example.onedaybetter.data

import android.content.Context
import com.example.onedaybetter.data.database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Data models (moved here to avoid duplication)
data class Habit(
    val id: Int,
    val name: String,
    val type: HabitType,
    val description: String = "",
    val weekProgress: List<Boolean> = List(7) { false },
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7)
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


data class HabitStats(
    val habitId: Int,
    val totalDays: Int,
    val completedDays: Int,
    val completionRate: Int,
    val currentStreak: Int,
    val completions: List<Pair<String, Boolean>>
)

data class Goal(
    val id: Int,
    val name: String,
    val description: String,
    val targetDate: String,
    val targetValue: String = "+85"
)

class DataRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val habitDao = database.habitDao()
    private val completionDao = database.habitCompletionDao()
    private val goalDao = database.goalDao()

    private var currentUserEmail: String? = null

    // User operations
    suspend fun loginUser(email: String) {
        currentUserEmail = email
        var user = userDao.getUserByEmail(email)
        if (user == null) {
            user = UserEntity(email = email)
            userDao.insert(user)
        }
    }

    suspend fun getCurrentUserEmail(): String? {
        if (currentUserEmail == null) {
            currentUserEmail = userDao.getCurrentUser()?.email
        }
        return currentUserEmail
    }

    // Habit operations
    suspend fun addHabit(
        name: String,
        type: HabitType,
        description: String,
        daysOfWeek: List<Int>
    ) {
        val email = getCurrentUserEmail() ?: return
        val habit = HabitEntity(
            userEmail = email,
            name = name,
            type = type.name,
            description = description,
            daysOfWeek = daysOfWeek.joinToString(",")
        )
        habitDao.insert(habit)
    }

    fun getAllHabits(): Flow<List<Habit>> {
        return kotlinx.coroutines.flow.flow {
            val email = getCurrentUserEmail() ?: return@flow
            habitDao.getHabitsByUser(email).collect { entities ->
                val habits = entities.map { entity ->
                    val weekProgress = getWeekProgress(entity.id)
                    Habit(
                        id = entity.id,
                        name = entity.name,
                        type = HabitType.valueOf(entity.type),
                        description = entity.description,
                        weekProgress = weekProgress,
                        daysOfWeek = entity.daysOfWeek.split(",")
                            .mapNotNull { it.toIntOrNull() }
                    )
                }
                emit(habits)
            }
        }
    }

    suspend fun getHabitsForDate(date: LocalDate): List<Habit> {
        val email = getCurrentUserEmail() ?: return emptyList()
        val dayOfWeek = date.dayOfWeek.value // 1=Monday, 7=Sunday
        val entities = habitDao.getHabitsForDay(email, dayOfWeek).first()

        return entities.map { entity ->
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val completion = completionDao.getCompletion(entity.id, dateStr)

            Habit(
                id = entity.id,
                name = entity.name,
                type = HabitType.valueOf(entity.type),
                description = entity.description,
                weekProgress = listOf(completion?.completed ?: false),
                daysOfWeek = entity.daysOfWeek.split(",")
                    .mapNotNull { it.toIntOrNull() }
            )
        }
    }

    private suspend fun getWeekProgress(habitId: Int): List<Boolean> {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)

        return (0..6).map { dayOffset ->
            val date = startOfWeek.plusDays(dayOffset.toLong())
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val completion = completionDao.getCompletion(habitId, dateStr)
            completion?.completed ?: false
        }
    }

    suspend fun toggleHabitCompletion(habitId: Int, date: LocalDate) {
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val existing = completionDao.getCompletion(habitId, dateStr)

        if (existing != null) {
            completionDao.updateCompletion(habitId, dateStr, !existing.completed)
        } else {
            completionDao.insert(
                HabitCompletionEntity(
                    habitId = habitId,
                    date = dateStr,
                    completed = true
                )
            )
        }
    }

    // Goal operations
    suspend fun addGoal(
        name: String,
        description: String,
        targetDate: String,
        targetValue: String
    ) {
        val email = getCurrentUserEmail() ?: return
        val goal = GoalEntity(
            userEmail = email,
            name = name,
            description = description,
            targetDate = targetDate,
            targetValue = targetValue
        )
        goalDao.insert(goal)
    }

    fun getAllGoals(): Flow<List<Goal>> {
        return kotlinx.coroutines.flow.flow {
            val email = getCurrentUserEmail() ?: return@flow
            goalDao.getGoalsByUser(email).collect { entities ->
                val goals = entities.map { entity ->
                    Goal(
                        id = entity.id,
                        name = entity.name,
                        description = entity.description,
                        targetDate = entity.targetDate,
                        targetValue = entity.targetValue
                    )
                }
                emit(goals)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: DataRepository? = null

        fun getInstance(context: Context): DataRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = DataRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}