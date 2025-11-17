package com.example.onedaybetter.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Update
    suspend fun update(user: UserEntity)
}

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE userEmail = :email ORDER BY createdAt DESC")
    fun getHabitsByUser(email: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Int): HabitEntity?

    @Query("SELECT * FROM habits WHERE userEmail = :email AND daysOfWeek LIKE '%' || :dayOfWeek || '%'")
    fun getHabitsForDay(email: String, dayOfWeek: Int): Flow<List<HabitEntity>>

    @Query("SELECT COUNT(*) FROM habits WHERE userEmail = :email")
    suspend fun getHabitsCount(email: String): Int
}

@Dao
interface HabitCompletionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completion: HabitCompletionEntity)

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getCompletion(habitId: Int, date: String): HabitCompletionEntity?

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY date DESC")
    fun getCompletionsByHabit(habitId: Int): Flow<List<HabitCompletionEntity>>

    @Query("""
        SELECT * FROM habit_completions 
        WHERE habitId = :habitId 
        AND date >= :startDate 
        AND date <= :endDate 
        ORDER BY date ASC
    """)
    suspend fun getCompletionsInRange(
        habitId: Int,
        startDate: String,
        endDate: String
    ): List<HabitCompletionEntity>

    @Query("UPDATE habit_completions SET completed = :completed WHERE habitId = :habitId AND date = :date")
    suspend fun updateCompletion(habitId: Int, date: String, completed: Boolean)

    @Query("""
        SELECT COUNT(*) FROM habit_completions 
        WHERE habitId = :habitId 
        AND completed = 1
        AND date >= :startDate 
        AND date <= :endDate
    """)
    suspend fun getCompletionCount(habitId: Int, startDate: String, endDate: String): Int
}

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)

    @Delete
    suspend fun delete(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE userEmail = :email ORDER BY createdAt DESC")
    fun getGoalsByUser(email: String): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Int): GoalEntity?

    @Query("SELECT * FROM goals WHERE userEmail = :email AND daysOfWeek LIKE '%' || :dayOfWeek || '%'")
    fun getGoalsForDay(email: String, dayOfWeek: Int): Flow<List<GoalEntity>>

    @Query("SELECT COUNT(*) FROM goals WHERE userEmail = :email")
    suspend fun getGoalsCount(email: String): Int
}

@Dao
interface GoalCompletionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completion: GoalCompletionEntity)

    @Query("SELECT * FROM goal_completions WHERE goalId = :goalId AND date = :date LIMIT 1")
    suspend fun getCompletion(goalId: Int, date: String): GoalCompletionEntity?

    @Query("SELECT * FROM goal_completions WHERE goalId = :goalId ORDER BY date DESC")
    fun getCompletionsByGoal(goalId: Int): Flow<List<GoalCompletionEntity>>

    @Query("UPDATE goal_completions SET completed = :completed WHERE goalId = :goalId AND date = :date")
    suspend fun updateCompletion(goalId: Int, date: String, completed: Boolean)
}