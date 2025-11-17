package com.example.onedaybetter.ui.addgoal

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.onedaybetter.data.getDisplayName
import com.example.onedaybetter.data.getIconVector
import com.example.onedaybetter.ui.home.BottomNavigationBar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    onGoalAdded: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToGoals: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    var goalName by remember { mutableStateOf("") }
    var goalDescription by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(HabitType.VALUE) }
    var expanded by remember { mutableStateOf(false) }
    var selectedDateOption by remember { mutableStateOf("En un mes") }
    var showDateMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = if (isDark) Color(0xFF000000) else Color.White,
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            Text(
                text = "Define tu meta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Meta",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color.White else Color.Black
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = goalName,
                onValueChange = { goalName = it },
                placeholder = {
                    Text(
                        "Nombre de la meta",
                        color = if (isDark) Color.Gray else Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isDark) Color.White else Color.Black,
                    unfocusedTextColor = if (isDark) Color.White else Color.Black,
                    focusedBorderColor = if (isDark) Color.White else Color.Black,
                    unfocusedBorderColor = if (isDark) Color.Gray else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    cursorColor = if (isDark) Color.White else Color.Black
                )
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Tipo de meta",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color.White else Color.Black
            )

            Spacer(Modifier.height(8.dp))

            Box {
                OutlinedTextField(
                    value = selectedType.getDisplayName(),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = selectedType.getIconVector(),
                            contentDescription = null,
                            tint = if (isDark) Color.White else Color.Black
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (isDark) Color.White else Color.Black
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDark) Color.White else Color.Black,
                        unfocusedTextColor = if (isDark) Color.White else Color.Black,
                        focusedBorderColor = if (isDark) Color.White else Color.Black,
                        unfocusedBorderColor = if (isDark) Color.Gray else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    HabitType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = type.getIconVector(),
                                        contentDescription = null,
                                        tint = if (isDark) Color.White else Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        type.getDisplayName(),
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                }
                            },
                            onClick = {
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Fecha objetivo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color.White else Color.Black
            )

            Spacer(Modifier.height(8.dp))

            Box {
                OutlinedTextField(
                    value = selectedDateOption,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDateMenu = !showDateMenu }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (isDark) Color.White else Color.Black
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDark) Color.White else Color.Black,
                        unfocusedTextColor = if (isDark) Color.White else Color.Black,
                        focusedBorderColor = if (isDark) Color.White else Color.Black,
                        unfocusedBorderColor = if (isDark) Color.Gray else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                DropdownMenu(
                    expanded = showDateMenu,
                    onDismissRequest = { showDateMenu = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    listOf("Hoy", "Mañana", "En una semana", "En un mes", "En 3 meses").forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    color = if (isDark) Color.White else Color.Black
                                )
                            },
                            onClick = {
                                selectedDateOption = option
                                showDateMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Descripción",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color.White else Color.Black
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = goalDescription,
                onValueChange = { goalDescription = it },
                placeholder = {
                    Text(
                        "Descripción (opcional)",
                        color = if (isDark) Color.Gray else Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isDark) Color.White else Color.Black,
                    unfocusedTextColor = if (isDark) Color.White else Color.Black,
                    focusedBorderColor = if (isDark) Color.White else Color.Black,
                    unfocusedBorderColor = if (isDark) Color.Gray else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    cursorColor = if (isDark) Color.White else Color.Black
                ),
                maxLines = 4
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (goalName.isNotBlank()) {
                        val targetDate = when(selectedDateOption) {
                            "Hoy" -> LocalDate.now()
                            "Mañana" -> LocalDate.now().plusDays(1)
                            "En una semana" -> LocalDate.now().plusWeeks(1)
                            "En un mes" -> LocalDate.now().plusMonths(1)
                            "En 3 meses" -> LocalDate.now().plusMonths(3)
                            else -> LocalDate.now().plusMonths(1)
                        }.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                        scope.launch {
                            repository.addGoal(
                                name = goalName,
                                type = selectedType,
                                description = goalDescription,
                                targetDate = targetDate
                            )
                            onGoalAdded()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color.White else Color.Black,
                    disabledContainerColor = if (isDark) Color.Gray else Color.Gray
                ),
                enabled = goalName.isNotBlank()
            ) {
                Text(
                    text = "Agregar meta",
                    fontSize = 16.sp,
                    color = if (isDark) Color.Black else Color.White
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}