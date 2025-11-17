package com.example.onedaybetter.ui.goals

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
import com.example.onedaybetter.data.Goal
import com.example.onedaybetter.ui.habitdetail.getIconVector
import com.example.onedaybetter.ui.home.BottomNavigationBar
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsListScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToAddGoal: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToGoalDetail: (Int) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val goals by repository.getAllGoals().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.value % 7
    val startOfWeek = today.minusDays(dayOfWeek.toLong())

    Scaffold(
        containerColor = if (isDark) Color(0xFF000000) else Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Metas",
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
                containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF5F5F5),
                contentColor = if (isDark) Color.White else Color.Black,
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
        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                }

                items(goals) { goal ->
                    ModernGoalCard(
                        goal = goal,
                        isDark = isDark,
                        startOfWeek = startOfWeek,
                        onClick = { onNavigateToGoalDetail(goal.id) },
                        onDelete = {
                            scope.launch {
                                repository.deleteGoal(goal.id)
                            }
                        },
                        onDayToggle = { date ->
                            scope.launch {
                                repository.toggleGoalCompletion(goal.id, date)
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
fun ModernGoalCard(
    goal: Goal,
    isDark: Boolean,
    startOfWeek: LocalDate,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onDayToggle: (LocalDate) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF8F8F8),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color.Black
                )

                Spacer(Modifier.height(4.dp))

                val daysText = when {
                    goal.daysOfWeek.size == 7 -> "Todos los días"
                    else -> goal.daysOfWeek.joinToString(" - ") { dayNum ->
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
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = goal.type.getIconVector(),
                    contentDescription = null,
                    tint = if (isDark) Color.White else Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

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
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (0..6).forEach { dayOffset ->
                val date = startOfWeek.plusDays(dayOffset.toLong())
                val dayNumber = date.dayOfMonth

                val dayOfWeekNum = if (date.dayOfWeek.value == 7) 7 else date.dayOfWeek.value
                val isActiveDay = goal.daysOfWeek.contains(dayOfWeekNum)

                val createdDate = LocalDate.ofEpochDay(goal.createdAt / (24 * 60 * 60 * 1000))
                val today = LocalDate.now()

                val dateToIndexMap = mutableMapOf<LocalDate, Int>()
                var currentDate = createdDate
                var index = 0

                while (!currentDate.isAfter(today)) {
                    val checkDayOfWeek = if (currentDate.dayOfWeek.value == 7) 7 else currentDate.dayOfWeek.value

                    if (goal.daysOfWeek.contains(checkDayOfWeek)) {
                        dateToIndexMap[currentDate] = index
                        index++
                    }
                    currentDate = currentDate.plusDays(1)
                }

                val isCompleted = if (isActiveDay && !date.isBefore(createdDate) && !date.isAfter(today)) {
                    val completionIndex = dateToIndexMap[date]
                    completionIndex?.let { goal.weekProgress.getOrNull(it) ?: false } ?: false
                } else {
                    false
                }

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
                        )
                        .clickable(
                            enabled = isActiveDay && !date.isAfter(today),
                            onClick = {
                                if (isActiveDay && !date.isAfter(today)) {
                                    onDayToggle(date)
                                }
                            }
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    text = "${goal.getCompletionPercentage()}%",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Meta: ${goal.targetDate}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más opciones",
                        tint = if (isDark) Color.White else Color.Black,
                        modifier = Modifier.size(20.dp)
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
}