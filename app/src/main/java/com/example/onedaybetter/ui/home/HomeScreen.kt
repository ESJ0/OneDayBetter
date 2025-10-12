package com.example.onedaybetter.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.Habit
import com.example.onedaybetter.data.HabitRepository
import com.example.onedaybetter.data.getIcon
import com.example.onedaybetter.ui.theme.OneDayBetterTheme
import java.util.*

@Composable
fun HomeScreen(
    onNavigateToHabits: () -> Unit,
    onNavigateToGoals: () -> Unit
) {
    val repository = HabitRepository.getInstance()
    val habits = repository.getAllHabits()
    val currentDate = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(currentDate.get(Calendar.DAY_OF_MONTH)) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 0,
                onTabSelected = { tab ->
                    when(tab) {
                        1 -> onNavigateToHabits()
                        2 -> onNavigateToGoals()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Month selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Agosto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )

                Icon(
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_month),
                    contentDescription = "Calendario",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Days of week
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Do").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Days numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..7).forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (day == selectedDate) Color.Black
                                else Color(0xFFF5F5F5)
                            )
                            .clickable { selectedDate = day },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.toString(),
                            fontSize = 14.sp,
                            color = if (day == selectedDate) Color.White else Color.Black,
                            fontWeight = if (day == selectedDate) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Habits list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(habits) { habit ->
                    HabitCard(habit = habit)
                }
            }
        }
    }
}

@Composable
fun HabitCard(habit: Habit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF5F5F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = habit.type.getIcon(),
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = habit.name,
                fontSize = 14.sp,
                color = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (habit.weekProgress[0]) Color.Black else Color.White,
                    CircleShape
                )
                .border(1.dp, Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (habit.weekProgress[0]) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completado",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { onTabSelected(0) }) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_sort_by_size),
                contentDescription = "Inicio",
                tint = if (selectedTab == 0) Color.Black else Color.Gray
            )
        }
        IconButton(onClick = { onTabSelected(1) }) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_save),
                contentDescription = "Hábitos",
                tint = if (selectedTab == 1) Color.Black else Color.Gray
            )
        }
        IconButton(onClick = { onTabSelected(2) }) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_edit),
                contentDescription = "Metas",
                tint = if (selectedTab == 2) Color.Black else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    OneDayBetterTheme {
        HomeScreen(
            onNavigateToHabits = {},
            onNavigateToGoals = {}
        )
    }
}