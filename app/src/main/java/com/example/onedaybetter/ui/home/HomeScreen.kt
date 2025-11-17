package com.example.onedaybetter.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.DataRepository
import com.example.onedaybetter.data.Goal
import com.example.onedaybetter.data.Habit
import com.example.onedaybetter.ui.habitdetail.getIconVector
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToHabits: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToHabitDetail: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var habits by remember { mutableStateOf<List<Habit>>(emptyList()) }
    var goals by remember { mutableStateOf<List<Goal>>(emptyList()) }
    var showCalendarDialog by remember { mutableStateOf(false) }

    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(selectedDate, refreshTrigger) {
        habits = repository.getHabitsForDate(selectedDate)
        goals = repository.getGoalsForDate(selectedDate)
    }

    Scaffold(
        containerColor = if (isDark) Color(0xFF000000) else Color.White,
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 0,
                onTabSelected = { tab ->
                    when (tab) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                        .replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color.White else Color.Black,
                    modifier = Modifier
                        .border(1.dp, if (isDark) Color.Gray else Color.Gray, RoundedCornerShape(8.dp))
                        .clickable { showCalendarDialog = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isDark) Color(0xFF2C2C2E) else Color(0xFFF5F5F5),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onNavigateToProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = if (isDark) Color.White else Color.Black
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            WeekDaysScroll(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                isDark = isDark
            )

            Spacer(Modifier.height(24.dp))

            if (habits.isEmpty() && goals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay hábitos ni metas para este día",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (habits.isNotEmpty()) {
                        item {
                            Text(
                                text = "Hábitos de hoy",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        }

                        items(habits) { habit ->
                            HabitCard(
                                habit = habit,
                                selectedDate = selectedDate,
                                isDark = isDark,
                                onToggle = {
                                    scope.launch {
                                        repository.toggleHabitCompletion(habit.id, selectedDate)
                                        habits = repository.getHabitsForDate(selectedDate)
                                    }
                                },
                                onClick = { onNavigateToHabitDetail() }
                            )
                        }
                    }

                    if (goals.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(8.dp))
                        }

                        item {
                            Text(
                                text = "Metas de hoy",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        }

                        items(goals) { goal ->
                            GoalCard(
                                goal = goal,
                                selectedDate = selectedDate,
                                isDark = isDark,
                                onToggle = {
                                    scope.launch {
                                        repository.toggleGoalCompletion(goal.id, selectedDate)
                                        goals = repository.getGoalsForDate(selectedDate)
                                    }
                                },
                                onClick = { onNavigateToHabitDetail() }
                            )
                        }
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showCalendarDialog) {
        CalendarDialog(
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                showCalendarDialog = false
            },
            onDismiss = { showCalendarDialog = false }
        )
    }
}

@Composable
fun WeekDaysScroll(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    isDark: Boolean
) {
    val dayOfWeek = selectedDate.dayOfWeek.value % 7
    val startOfWeek = selectedDate.minusDays(dayOfWeek.toLong())
    val daysOfWeek = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb").forEach { day ->
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { date ->
                val isSelected = date == selectedDate
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected)
                                if (isDark) Color.White else Color.Black
                            else
                                if (isDark) Color(0xFF2C2C2E) else Color(0xFFF5F5F5)
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        fontSize = 14.sp,
                        color = if (isSelected)
                            if (isDark) Color.Black else Color.White
                        else
                            if (isDark) Color.White else Color.Black,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Text("◀", fontSize = 20.sp)
                }

                Text(
                    text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                        .replaceFirstChar { it.uppercase() } + " ${currentMonth.year}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Text("▶", fontSize = 20.sp)
                }
            }
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("D", "L", "M", "X", "J", "V", "S").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                val firstDayOfMonth = currentMonth.atDay(1)
                val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
                val daysInMonth = currentMonth.lengthOfMonth()
                val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7

                Column {
                    for (week in 0 until (totalCells / 7)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (day in 0..6) {
                                val cellIndex = week * 7 + day
                                val dayOfMonth = cellIndex - firstDayOfWeek + 1

                                if (dayOfMonth in 1..daysInMonth) {
                                    val date = currentMonth.atDay(dayOfMonth)
                                    val isSelected = date == selectedDate
                                    val isToday = date == LocalDate.now()

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    isSelected -> Color.Black
                                                    isToday -> Color(0xFFE3F2FD)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable { onDateSelected(date) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dayOfMonth.toString(),
                                            fontSize = 14.sp,
                                            color = if (isSelected) Color.White else Color.Black,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                } else {
                                    Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun HabitCard(
    habit: Habit,
    selectedDate: LocalDate,
    isDark: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val isCompleted = habit.weekProgress.firstOrNull() ?: false

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isDark) Color(0xFF1C1C1E) else Color.White,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (isDark) Color(0xFF2C2C2E) else Color(0xFFE0E0E0),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable { onClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isDark) Color(0xFF2C2C2E) else Color(0xFFF5F5F5),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = habit.type.getIconVector(),
                    contentDescription = null,
                    tint = if (isDark) Color.White else Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = habit.name,
                    fontSize = 14.sp,
                    color = if (isDark) Color.White else Color.Black
                )
                Text(
                    text = "Hábito",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (isCompleted)
                        if (isDark) Color.White else Color.Black
                    else
                        if (isDark) Color(0xFF2C2C2E) else Color.White,
                    CircleShape
                )
                .border(
                    1.dp,
                    if (isDark) Color.Gray else Color.Gray,
                    CircleShape
                )
                .clickable(
                    onClick = { onToggle() },
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completado",
                    tint = if (isDark) Color.Black else Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    selectedDate: LocalDate,
    isDark: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val isCompleted = goal.weekProgress.firstOrNull() ?: false

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isDark) Color(0xFF1C1C1E) else Color.White,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (isDark) Color(0xFF2C2C2E) else Color(0xFFE0E0E0),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable { onClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isDark) Color(0xFF2C2C2E) else Color(0xFFF5F5F5),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = goal.type.getIconVector(),
                    contentDescription = null,
                    tint = if (isDark) Color.White else Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = goal.name,
                    fontSize = 14.sp,
                    color = if (isDark) Color.White else Color.Black
                )
                Text(
                    text = "Meta",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (isCompleted)
                        if (isDark) Color.White else Color.Black
                    else
                        if (isDark) Color(0xFF2C2C2E) else Color.White,
                    CircleShape
                )
                .border(
                    1.dp,
                    if (isDark) Color.Gray else Color.Gray,
                    CircleShape
                )
                .clickable(
                    onClick = { onToggle() },
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completado",
                    tint = if (isDark) Color.Black else Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val isDark = isSystemInDarkTheme()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isDark) Color(0xFF1C1C1E) else Color.White)
            .padding(vertical = 12.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { onTabSelected(0) }) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_sort_by_size),
                contentDescription = "Inicio",
                tint = if (selectedTab == 0)
                    if (isDark) Color.White else Color.Black
                else
                    Color.Gray
            )
        }
        IconButton(onClick = { onTabSelected(1) }) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_save),
                contentDescription = "Hábitos",
                tint = if (selectedTab == 1)
                    if (isDark) Color.White else Color.Black
                else
                    Color.Gray
            )
        }
        IconButton(onClick = { onTabSelected(2) }) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_edit),
                contentDescription = "Metas",
                tint = if (selectedTab == 2)
                    if (isDark) Color.White else Color.Black
                else
                    Color.Gray
            )
        }
    }
}