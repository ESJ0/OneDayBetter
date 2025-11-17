package com.example.onedaybetter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.onedaybetter.data.DataRepository
import com.example.onedaybetter.navigation.*
import com.example.onedaybetter.ui.addgoal.AddGoalScreen
import com.example.onedaybetter.ui.addhabit.AddHabitScreen
import com.example.onedaybetter.ui.goals.GoalsListScreen
import com.example.onedaybetter.ui.habitdetail.HabitDetailScreen
import com.example.onedaybetter.ui.habits.HabitsListScreen
import com.example.onedaybetter.ui.home.HomeScreen
import com.example.onedaybetter.ui.login.LoginScreen
import com.example.onedaybetter.ui.profile.ProfileScreen
import com.example.onedaybetter.ui.theme.OneDayBetterTheme
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    // Verificar si hay usuario logueado
    var startDestination: Any = Login
    LaunchedEffect(Unit) {
        scope.launch {
            val email = repository.getCurrentUserEmail()
            if (email != null) {
                startDestination = Home
            }
        }
    }

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
                onNavigateToHabitDetail = { },
                onNavigateToProfile = { navController.navigate(Profile) }
            )
        }

        composable<Profile> {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo(0) { inclusive = true }
                    }
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
                onNavigateToHabits = { navController.navigate(HabitsList) },
                onNavigateToGoalDetail = { goalId ->
                    navController.navigate(GoalDetail(goalId))
                }
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

        composable<GoalDetail> { backStackEntry ->
            val goalDetail = backStackEntry.toRoute<GoalDetail>()
            GoalDetailScreen(
                goalId = goalDetail.goalId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun GoalDetailScreen(
    goalId: Int,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    HabitDetailScreen(
        habitId = goalId,
        onNavigateBack = onNavigateBack
    )
}