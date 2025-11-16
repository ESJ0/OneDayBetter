package com.example.onedaybetter.data

import android.content.Context
import com.example.onedaybetter.data.database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Habit(
    val id: Int,
    val name: String,
    val type: HabitType,
    val description: String = "",
    val weekProgress: List<Boolean> = List(7) { false },
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getCompletionPercentage(): Int {
        val createdDate = LocalDate.ofEpochDay(createdAt / (24 * 60 * 60 * 1000))
        val today = LocalDate.now()

        // Contar días desde la creación hasta hoy que corresponden a los días activos
        var totalActiveDays = 0
        var currentDate = createdDate

        while (!currentDate.isAfter(today)) {
            val dayOfWeek = currentDate.dayOfWeek.value
            if (daysOfWeek.contains(dayOfWeek)) {
                totalActiveDays++
            }
            currentDate = currentDate.plusDays(1)
        }

        if (totalActiveDays == 0) return 0

        val completedDays = weekProgress.count { it }
        return (completedDays * 100) / totalActiveDays
    }
}

enum class HabitType {
    EXERCISE,
    SLEEP,
    FOOD,
    VALUE
}

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
            daysOfWeek = daysOfWeek.joinToString(","),
            createdAt = System.currentTimeMillis()
        )
        habitDao.insert(habit)
    }

    fun getAllHabits(): Flow<List<Habit>> {
        return kotlinx.coroutines.flow.flow {
            val email = getCurrentUserEmail() ?: return@flow
            habitDao.getHabitsByUser(email).collect { entities ->
                val habits = entities.map { entity ->
                    val allCompletions = getAllCompletionsForHabit(entity.id)
                    Habit(
                        id = entity.id,
                        name = entity.name,
                        type = HabitType.valueOf(entity.type),
                        description = entity.description,
                        weekProgress = allCompletions,
                        daysOfWeek = entity.daysOfWeek.split(",")
                            .mapNotNull { it.toIntOrNull() },
                        createdAt = entity.createdAt
                    )
                }
                emit(habits)
            }
        }
    }

    private suspend fun getAllCompletionsForHabit(habitId: Int): List<Boolean> {
        val entity = habitDao.getHabitById(habitId) ?: return emptyList()
        val createdDate = LocalDate.ofEpochDay(entity.createdAt / (24 * 60 * 60 * 1000))
        val today = LocalDate.now()
        val daysOfWeek = entity.daysOfWeek.split(",").mapNotNull { it.toIntOrNull() }

        val completions = mutableListOf<Boolean>()
        var currentDate = createdDate

        while (!currentDate.isAfter(today)) {
            val dayOfWeek = currentDate.dayOfWeek.value
            if (daysOfWeek.contains(dayOfWeek)) {
                val dateStr = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val completion = completionDao.getCompletion(habitId, dateStr)
                completions.add(completion?.completed ?: false)
            }
            currentDate = currentDate.plusDays(1)
        }

        return completions
    }

    suspend fun getHabitsForDate(date: LocalDate): List<Habit> {
        val email = getCurrentUserEmail() ?: return emptyList()
        val dayOfWeek = date.dayOfWeek.value
        val entities = habitDao.getHabitsForDay(email, dayOfWeek).first()

        return entities.map { entity ->
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val completion = completionDao.getCompletion(entity.id, dateStr)
            val allCompletions = getAllCompletionsForHabit(entity.id)

            Habit(
                id = entity.id,
                name = entity.name,
                type = HabitType.valueOf(entity.type),
                description = entity.description,
                weekProgress = listOf(completion?.completed ?: false),
                daysOfWeek = entity.daysOfWeek.split(",")
                    .mapNotNull { it.toIntOrNull() },
                createdAt = entity.createdAt
            ).copy(weekProgress = allCompletions)
        }
    }

    private suspend fun getWeekProgress(habitId: Int): List<Boolean> {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.value % 7 // Domingo = 0
        val startOfWeek = today.minusDays(dayOfWeek.toLong())

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