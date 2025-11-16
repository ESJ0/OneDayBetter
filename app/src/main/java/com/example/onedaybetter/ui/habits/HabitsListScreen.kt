package com.example.onedaybetter.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.DataRepository
import com.example.onedaybetter.data.Habit
import com.example.onedaybetter.ui.habitdetail.getIconVector
import com.example.onedaybetter.ui.home.BottomNavigationBar
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsListScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToAddHabit: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToHabitDetail: (Int) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    var habits by remember { mutableStateOf<List<Habit>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    // Calcular inicio de semana (domingo)
    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.value % 7
    val startOfWeek = today.minusDays(dayOfWeek.toLong())

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
                item {
                    Spacer(Modifier.height(8.dp))

                    // Encabezados de los días
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb").forEach { day ->
                            Text(
                                text = day,
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.width(44.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }

                items(habits) { habit ->
                    ModernHabitCard(
                        habit = habit,
                        isDark = isDark,
                        startOfWeek = startOfWeek,
                        onClick = { onNavigateToHabitDetail(habit.id) },
                        onDelete = {
                            scope.launch {
                                // TODO: Implementar eliminación
                            }
                        }
                    )
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ModernHabitCard(
    habit: Habit,
    isDark: Boolean,
    startOfWeek: LocalDate,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF8F8F8),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // Header: Nombre del hábito, días activos y menú
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono del hábito
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = habit.type.getIconVector(),
                        contentDescription = null,
                        tint = if (isDark) Color.White else Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Menú de tres puntos
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Más opciones",
                            tint = if (isDark) Color.White else Color.Black
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Eliminar") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Días de la semana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Ajustar weekProgress para iniciar en domingo
            val adjustedProgress = listOf(
                habit.weekProgress.getOrNull(6) ?: false, // Dom
                habit.weekProgress.getOrNull(0) ?: false, // Lun
                habit.weekProgress.getOrNull(1) ?: false, // Mar
                habit.weekProgress.getOrNull(2) ?: false, // Mié
                habit.weekProgress.getOrNull(3) ?: false, // Jue
                habit.weekProgress.getOrNull(4) ?: false, // Vie
                habit.weekProgress.getOrNull(5) ?: false  // Sáb
            )

            adjustedProgress.forEachIndexed { index, isCompleted ->
                val date = startOfWeek.plusDays(index.toLong())
                val dayNumber = date.dayOfMonth
                val dayOfWeekNum = (index + 7) % 7 + 1 // Convertir: 0=Dom(7), 1=Lun(1), etc.
                val isActiveDay = habit.daysOfWeek.contains(if (dayOfWeekNum == 7) 7 else dayOfWeekNum)

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

        Spacer(Modifier.height(16.dp))

        // Footer con porcentaje
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