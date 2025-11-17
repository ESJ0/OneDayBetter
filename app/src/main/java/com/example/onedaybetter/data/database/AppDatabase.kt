package com.example.onedaybetter.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        GoalEntity::class,
        GoalCompletionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun goalDao(): GoalDao
    abstract fun goalCompletionDao(): GoalCompletionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Agregar columna password a users
                database.execSQL("ALTER TABLE users ADD COLUMN password TEXT NOT NULL DEFAULT ''")

                // Recrear tabla goals con nueva estructura
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS goals_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userEmail TEXT NOT NULL,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        description TEXT NOT NULL,
                        targetDate TEXT NOT NULL,
                        daysOfWeek TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(userEmail) REFERENCES users(email) ON DELETE CASCADE
                    )
                """)

                // Copiar datos existentes
                database.execSQL("""
                    INSERT INTO goals_new (id, userEmail, name, type, description, targetDate, daysOfWeek, createdAt)
                    SELECT id, userEmail, name, 'VALUE', description, targetDate, '1,2,3,4,5,6,7', createdAt
                    FROM goals
                """)

                // Eliminar tabla vieja y renombrar
                database.execSQL("DROP TABLE goals")
                database.execSQL("ALTER TABLE goals_new RENAME TO goals")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_goals_userEmail ON goals(userEmail)")

                // Crear tabla goal_completions
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS goal_completions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        goalId INTEGER NOT NULL,
                        date TEXT NOT NULL,
                        completed INTEGER NOT NULL,
                        completedAt INTEGER NOT NULL,
                        FOREIGN KEY(goalId) REFERENCES goals(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_goal_completions_goalId ON goal_completions(goalId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_goal_completions_date ON goal_completions(date)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "onedaybetter_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}