package com.example.onedaybetter.data

class GoalRepository {
    private val goals = mutableListOf<Goal>()
    private var nextId = 1

    fun getAllGoals(): List<Goal> = goals.toList()

    fun addGoal(name: String, description: String, targetDate: String, targetValue: String) {
        val newGoal = Goal(
            id = nextId++,
            name = name,
            description = description,
            targetDate = targetDate,
            targetValue = targetValue
        )
        goals.add(newGoal)
    }

    fun getGoalById(id: Int): Goal? = goals.firstOrNull { it.id == id }

    companion object {
        private var instance: GoalRepository? = null

        fun getInstance(): GoalRepository {
            if (instance == null) {
                instance = GoalRepository()
            }
            return instance!!
        }
    }
}