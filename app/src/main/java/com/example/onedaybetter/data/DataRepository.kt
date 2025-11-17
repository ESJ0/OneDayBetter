package com.example.onedaybetter.data

import android.content.Context
import android.content.SharedPreferences
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

        var totalActiveDays = 0
        var currentDate = createdDate

        while (!currentDate.isAfter(today)) {
            val dayOfWeek = if (currentDate.dayOfWeek.value == 7) 7 else currentDate.dayOfWeek.value
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
    val type: HabitType,
    val description: String,
    val targetDate: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isActive(): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val target = LocalDate.parse(targetDate, formatter)
            val today = LocalDate.now()
            !today.isAfter(target)
        } catch (e: Exception) {
            false
        }
    }

    fun getDaysUntilTarget(): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val target = LocalDate.parse(targetDate, formatter)
            val today = LocalDate.now()
            java.time.temporal.ChronoUnit.DAYS.between(today, target).toInt()
        } catch (e: Exception) {
            0
        }
    }
}

class DataRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val habitDao = database.habitDao()
    private val completionDao = database.habitCompletionDao()
    private val goalDao = database.goalDao()
    private val goalCompletionDao = database.goalCompletionDao()

    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private var currentUserEmail: String? = null

    // MutableStateFlow para forzar actualizaciones
    private val _habitsUpdateTrigger = kotlinx.coroutines.flow.MutableStateFlow(0)
    private val habitsUpdateTrigger = _habitsUpdateTrigger

    suspend fun registerUser(email: String, password: String, name: String): Boolean {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            return false
        }
        val user = UserEntity(email = email, password = password, name = name)
        userDao.insert(user)
        return true
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        val user = userDao.getUserByEmailAndPassword(email, password)
        if (user != null) {
            currentUserEmail = email
            prefs.edit().putString("logged_user", email).apply()
            return true
        }
        return false
    }

    suspend fun getCurrentUserEmail(): String? {
        if (currentUserEmail == null) {
            currentUserEmail = prefs.getString("logged_user", null)
        }
        return currentUserEmail
    }

    suspend fun getCurrentUser(): UserEntity? {
        val email = getCurrentUserEmail() ?: return null
        return userDao.getUserByEmail(email)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.update(user)
    }

    fun logoutUser() {
        currentUserEmail = null
        prefs.edit().remove("logged_user").apply()
    }

    suspend fun getHabitsCount(): Int {
        val email = getCurrentUserEmail() ?: return 0
        return habitDao.getHabitsCount(email)
    }

    suspend fun getGoalsCount(): Int {
        val email = getCurrentUserEmail() ?: return 0
        return goalDao.getGoalsCount(email)
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

    suspend fun deleteHabit(habitId: Int) {
        val habit = habitDao.getHabitById(habitId) ?: return
        habitDao.delete(habit)
    }

    fun getAllHabits(): Flow<List<Habit>> {
        return kotlinx.coroutines.flow.flow {
            val email = getCurrentUserEmail() ?: return@flow
            habitDao.getHabitsByUser(email).collect { entities ->
                val habits = entities.map { entity ->
                    // Siempre recalcular las completaciones desde la base de datos
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
            val dayOfWeek = if (currentDate.dayOfWeek.value == 7) 7 else currentDate.dayOfWeek.value
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
        val dayOfWeek = if (date.dayOfWeek.value == 7) 7 else date.dayOfWeek.value
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
                    .mapNotNull { it.toIntOrNull() },
                createdAt = entity.createdAt
            )
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
        type: HabitType,
        description: String,
        targetDate: String
    ) {
        val email = getCurrentUserEmail() ?: return
        val goal = GoalEntity(
            userEmail = email,
            name = name,
            type = type.name,
            description = description,
            targetDate = targetDate,
            daysOfWeek = "", // No se usan días de semana
            createdAt = System.currentTimeMillis()
        )
        goalDao.insert(goal)
    }

    suspend fun deleteGoal(goalId: Int) {
        val goal = goalDao.getGoalById(goalId) ?: return
        goalDao.delete(goal)
    }

    fun getAllGoals(): Flow<List<Goal>> {
        return kotlinx.coroutines.flow.flow {
            val email = getCurrentUserEmail() ?: return@flow
            goalDao.getGoalsByUser(email).collect { entities ->
                val goals = entities.map { entity ->
                    Goal(
                        id = entity.id,
                        name = entity.name,
                        type = HabitType.valueOf(entity.type),
                        description = entity.description,
                        targetDate = entity.targetDate,
                        createdAt = entity.createdAt
                    )
                }
                // Filtrar solo las metas activas
                emit(goals.filter { it.isActive() })
            }
        }
    }

    suspend fun getActiveGoals(): List<Goal> {
        val email = getCurrentUserEmail() ?: return emptyList()
        val entities = goalDao.getGoalsByUser(email).first()

        return entities.map { entity ->
            Goal(
                id = entity.id,
                name = entity.name,
                type = HabitType.valueOf(entity.type),
                description = entity.description,
                targetDate = entity.targetDate,
                createdAt = entity.createdAt
            )
        }.filter { it.isActive() }
    }

    // Métodos que se mantienen para compatibilidad (aunque las metas ya no usan días)
    suspend fun getGoalsForDate(date: LocalDate): List<Goal> {
        return getActiveGoals()
    }

    suspend fun toggleGoalCompletion(goalId: Int, date: LocalDate) {
        // Las metas ya no tienen sistema de completado diario
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