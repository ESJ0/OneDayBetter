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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.DataRepository
import com.example.onedaybetter.data.Goal
import com.example.onedaybetter.data.getIconVector
import com.example.onedaybetter.ui.home.BottomNavigationBar
import kotlinx.coroutines.launch

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
                containerColor = if (isDark) Color.White else Color.Black,
                contentColor = if (isDark) Color.Black else Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar meta",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
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
                    GoalCard(
                        goal = goal,
                        isDark = isDark,
                        onClick = { onNavigateToGoalDetail(goal.id) },
                        onDelete = {
                            scope.launch {
                                repository.deleteGoal(goal.id)
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
fun GoalCard(
    goal: Goal,
    isDark: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val daysUntilTarget = goal.getDaysUntilTarget()

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
        Row(
            modifier = Modifier.fillMaxWidth(),
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

                Spacer(Modifier.height(8.dp))

                Text(
                    text = when {
                        daysUntilTarget < 0 -> "Meta vencida"
                        daysUntilTarget == 0 -> "Vence hoy"
                        daysUntilTarget == 1 -> "Vence mañana"
                        daysUntilTarget < 7 -> "Vence en $daysUntilTarget días"
                        daysUntilTarget < 30 -> "Vence en ${daysUntilTarget / 7} semanas"
                        else -> "Vence en ${daysUntilTarget / 30} meses"
                    },
                    fontSize = 12.sp,
                    color = when {
                        daysUntilTarget < 0 -> Color.Red
                        daysUntilTarget < 7 -> Color(0xFFFF9800)
                        else -> Color.Gray
                    },
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

        if (goal.description.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = goal.description,
                fontSize = 14.sp,
                color = if (isDark) Color.Gray else Color.Gray,
                maxLines = 2
            )
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
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_my_calendar),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(6.dp))

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