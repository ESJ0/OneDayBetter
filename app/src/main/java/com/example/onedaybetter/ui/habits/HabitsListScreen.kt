package com.example.onedaybetter.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.DataRepository
import com.example.onedaybetter.data.Habit
import com.example.onedaybetter.data.getIcon
import com.example.onedaybetter.ui.home.BottomNavigationBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsListScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToAddHabit: () -> Unit,
    onNavigateToGoals: () -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    var habits by remember { mutableStateOf<List<Habit>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        scope.launch {
            repository.getAllHabits().collect { habitsList ->
                habits = habitsList
            }
        }
    }

    Scaffold(
        containerColor = if (isDark) Color(0xFF000000) else Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hábitos",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF000000) else Color.White
                )
            )
        },
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
                containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF5F5F5),
                contentColor = if (isDark) Color.White else Color.Black,
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
        if (habits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay hábitos aún",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                items(habits) { habit ->
                    ModernHabitCard(
                        habit = habit,
                        isDark = isDark
                    )
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ModernHabitCard(habit: Habit, isDark: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF8F8F8),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // Header: Nombre del hábito y días activos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color.Black
                )

                Spacer(Modifier.height(4.dp))

                // Texto de días activos
                val daysText = when {
                    habit.daysOfWeek.size == 7 -> "Todos los días"
                    else -> habit.daysOfWeek.joinToString(" - ") { dayNum ->
                        when(dayNum) {
                            1 -> "Lun"
                            2 -> "Mar"
                            3 -> "Mié"
                            4 -> "Jue"
                            5 -> "Vie"
                            6 -> "Sáb"
                            7 -> "Dom"
                            else -> ""
                        }
                    }
                }

                Text(
                    text = daysText,
                    fontSize = 12.sp,
                    color = Color(0xFF000000),
                    fontWeight = FontWeight.Medium
                )
            }

            // Icono del hábito sin fondo
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = habit.type.getIcon(),
                    fontSize = 32.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Días de la semana con etiquetas
        Column {
            // Labels: Dom, Lun, Mar, etc.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.width(44.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Círculos con números de días
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                habit.weekProgress.forEachIndexed { index, isCompleted ->
                    val dayNumber = index + 9 // Empieza desde 9
                    val isActiveDay = habit.daysOfWeek.contains(index + 1)

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = when {
                                    !isActiveDay -> if (isDark) Color(0xFF2C2C2E) else Color(0xFFE8E8E8)
                                    isCompleted -> Color(0xFF000000)
                                    else -> if (isDark) Color(0xFF2C2C2E) else Color(0xFFE8E8E8)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayNumber.toString(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = when {
                                !isActiveDay -> Color.Gray
                                isCompleted -> Color.White
                                else -> if (isDark) Color.Gray else Color.Gray
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Footer con solo porcentaje
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.checkbox_on_background),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = "${habit.getCompletionPercentage()}%",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}