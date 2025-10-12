

package com.example.onedaybetter.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.Habit
import com.example.onedaybetter.data.HabitRepository
import com.example.onedaybetter.data.getIcon
import com.example.onedaybetter.ui.home.BottomNavigationBar
import com.example.onedaybetter.ui.theme.OneDayBetterTheme

@Composable
fun HabitsListScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToAddHabit: () -> Unit,
    onNavigateToGoals: () -> Unit
) {
    val repository = HabitRepository.getInstance()
    val habits = repository.getAllHabits()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 1,
                onTabSelected = { tab ->
                    when(tab) {
                        0 -> onNavigateToHome()
                        2 -> onNavigateToGoals()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddHabit,
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar hábito",
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
                text = "Habitos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(habits) { habit ->
                    HabitDetailCard(habit = habit)
                }
            }
        }
    }
}

@Composable
fun HabitDetailCard(habit: Habit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = habit.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFF5F5F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = habit.type.getIcon(),
                    fontSize = 18.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab").forEach { index ->
                val dayIndex = listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab").indexOf(index)
                val isCompleted = habit.weekProgress.getOrNull(dayIndex) ?: false

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isCompleted) Color(0xFF4CAF50) else Color(0xFFEF5350),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (dayIndex + 1).toString(),
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${habit.getCompletionPercentage()}%",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitsListScreenPreview() {
    OneDayBetterTheme {
        HabitsListScreen(
            onNavigateToHome = {},
            onNavigateToAddHabit = {},
            onNavigateToGoals = {}
        )
    }
}