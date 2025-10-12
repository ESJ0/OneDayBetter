package com.example.onedaybetter.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.Goal
import com.example.onedaybetter.data.GoalRepository
import com.example.onedaybetter.ui.home.BottomNavigationBar
import com.example.onedaybetter.ui.theme.OneDayBetterTheme

@Composable
fun GoalsListScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToAddGoal: () -> Unit,
    onNavigateToHabits: () -> Unit
) {
    val repository = GoalRepository.getInstance()
    val goals = repository.getAllGoals()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 2,
                onTabSelected = { tab ->
                    when(tab) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToHabits()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddGoal,
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar meta",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Meta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(24.dp))

            if (goals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay metas aún",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(goals) { goal ->
                        GoalCard(goal = goal)
                    }
                }
            }
        }
    }
}

@Composable
fun GoalCard(goal: Goal) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Obtener promedio de ${goal.targetValue}",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = goal.description.ifBlank {
                "Texto descriptivo donde se especificará las características de la meta a obtener."
            },
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = goal.targetDate,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun GoalsListScreenPreview() {
    OneDayBetterTheme {
        GoalsListScreen(
            onNavigateToHome = {},
            onNavigateToAddGoal = {},
            onNavigateToHabits = {}
        )
    }
}

@Preview(showBackground = true, name = "With Data")
@Composable
fun GoalsListScreenWithDataPreview() {
    OneDayBetterTheme {
        Column {
            GoalCard(
                goal = Goal(
                    id = 1,
                    name = "Meta de ejemplo",
                    description = "Texto descriptivo donde se especificará las características de la meta a obtener.",
                    targetDate = "31/11/2025",
                    targetValue = "+85"
                )
            )
        }
    }
}