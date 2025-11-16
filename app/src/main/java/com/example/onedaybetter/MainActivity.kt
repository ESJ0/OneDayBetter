package com.example.onedaybetter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.onedaybetter.navigation.*
import com.example.onedaybetter.ui.addgoal.AddGoalScreen
import com.example.onedaybetter.ui.addhabit.AddHabitScreen
import com.example.onedaybetter.ui.goals.GoalsListScreen
import com.example.onedaybetter.ui.habitdetail.HabitDetailScreen
import com.example.onedaybetter.ui.habits.HabitsListScreen
import com.example.onedaybetter.ui.home.HomeScreen
import com.example.onedaybetter.ui.login.LoginScreen
import com.example.onedaybetter.ui.theme.OneDayBetterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OneDayBetterTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Home) {
                        popUpTo<Login> { inclusive = true }
                    }
                }
            )
        }

        composable<Home> {
            HomeScreen(
                onNavigateToHabits = { navController.navigate(HabitsList) },
                onNavigateToGoals = { navController.navigate(GoalsList) },
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate(HabitDetail(habitId))
                }
            )
        }

        composable<HabitsList> {
            HabitsListScreen(
                onNavigateToHome = { navController.navigate(Home) },
                onNavigateToAddHabit = { navController.navigate(AddHabit) },
                onNavigateToGoals = { navController.navigate(GoalsList) },
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate(HabitDetail(habitId))
                }
            )
        }

        composable<AddHabit> {
            AddHabitScreen(
                onHabitAdded = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Home) },
                onNavigateToHabits = { navController.navigate(HabitsList) },
                onNavigateToGoals = { navController.navigate(GoalsList) }
            )
        }

        composable<HabitDetail> { backStackEntry ->
            val habitDetail = backStackEntry.toRoute<HabitDetail>()
            HabitDetailScreen(
                habitId = habitDetail.habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<GoalsList> {
            GoalsListScreen(
                onNavigateToHome = { navController.navigate(Home) },
                onNavigateToAddGoal = { navController.navigate(AddGoal) },
                onNavigateToHabits = { navController.navigate(HabitsList) }
            )
        }

        composable<AddGoal> {
            AddGoalScreen(
                onGoalAdded = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Home) },
                onNavigateToHabits = { navController.navigate(HabitsList) },
                onNavigateToGoals = { navController.navigate(GoalsList) }
            )
        }
    }
}