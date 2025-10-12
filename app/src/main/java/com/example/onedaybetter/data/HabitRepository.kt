package com.example.onedaybetter.data

class HabitRepository {
    private val habits = mutableListOf(
        Habit(
            id = 1,
            name = "Correr 30 minutos",
            type = HabitType.EXERCISE,
            description = "",
            weekProgress = listOf(true, true, true, false, false, true, false)
        ),
        Habit(
            id = 2,
            name = "Dormir 8 horas",
            type = HabitType.SLEEP,
            description = "",
            weekProgress = listOf(true, true, true, true, false, true, false)
        ),
        Habit(
            id = 3,
            name = "comer saludable",
            type = HabitType.FOOD,
            description = "",
            weekProgress = listOf(false, true, true, true, false, true, false)
        )
    )
    private var nextId = 4

    fun getAllHabits(): List<Habit> = habits.toList()

    fun addHabit(name: String, type: HabitType, description: String) {
        val newHabit = Habit(
            id = nextId++,
            name = name,
            type = type,
            description = description,
            weekProgress = List(7) { false }
        )
        habits.add(newHabit)
    }

    fun getHabitById(id: Int): Habit? = habits.firstOrNull { it.id == id }

    companion object {
        private var instance: HabitRepository? = null

        fun getInstance(): HabitRepository {
            if (instance == null) {
                instance = HabitRepository()
            }
            return instance!!
        }
    }
}