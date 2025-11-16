package com.example.onedaybetter.ui.addhabit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import com.example.onedaybetter.data.HabitType
import com.example.onedaybetter.ui.home.BottomNavigationBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    onHabitAdded: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToGoals: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    var habitName by remember { mutableStateOf("") }
    var habitDescription by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Value") }
    var expanded by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf(setOf(1, 2, 3, 4, 5, 6, 7)) }

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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                text = "Define tu hábito",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Hábito",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = habitName,
                onValueChange = { habitName = it },
                placeholder = { Text("Nombre del hábito", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Tipo de hábito",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Box {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Value", "Exercise", "Sleep", "Food").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedType = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Días de la semana",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val daysLabels = listOf("L", "M", "X", "J", "V", "S", "D")
                daysLabels.forEachIndexed { index, day ->
                    val dayNumber = index + 1
                    val isSelected = selectedDays.contains(dayNumber)

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (isSelected) Color.Black else Color.White,
                                CircleShape
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color.Black else Color.Gray,
                                CircleShape
                            )
                            .clickable {
                                selectedDays = if (isSelected) {
                                    selectedDays - dayNumber
                                } else {
                                    selectedDays + dayNumber
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else Color.Black,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Descripción",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = habitDescription,
                onValueChange = { habitDescription = it },
                placeholder = { Text("Descripción (opcional)", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (habitName.isNotBlank() && selectedDays.isNotEmpty()) {
                        val type = when(selectedType) {
                            "Exercise" -> HabitType.EXERCISE
                            "Sleep" -> HabitType.SLEEP
                            "Food" -> HabitType.FOOD
                            else -> HabitType.VALUE
                        }

                        scope.launch {
                            repository.addHabit(
                                name = habitName,
                                type = type,
                                description = habitDescription,
                                daysOfWeek = selectedDays.toList()
                            )
                            onHabitAdded()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                enabled = habitName.isNotBlank() && selectedDays.isNotEmpty()
            ) {
                Text(
                    text = "Agregar hábito",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}