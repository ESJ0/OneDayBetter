package com.example.onedaybetter.ui.addhabit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.HabitRepository
import com.example.onedaybetter.data.HabitType
import com.example.onedaybetter.ui.home.BottomNavigationBar
import com.example.onedaybetter.ui.theme.OneDayBetterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    onHabitAdded: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToGoals: () -> Unit
) {
    var habitName by remember { mutableStateOf("") }
    var habitDescription by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Value") }
    var expanded by remember { mutableStateOf(false) }
    val repository = HabitRepository.getInstance()

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
                text = "Define tu habito",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Habito",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = habitName,
                onValueChange = { habitName = it },
                placeholder = { Text("Nombre del habito", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Tipo de habito",
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
                text = "Descripcion",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = habitDescription,
                onValueChange = { habitDescription = it },
                placeholder = { Text("Descripcion (opcional)", color = Color.Gray) },
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
                    if (habitName.isNotBlank()) {
                        val type = when(selectedType) {
                            "Exercise" -> HabitType.EXERCISE
                            "Sleep" -> HabitType.SLEEP
                            "Food" -> HabitType.FOOD
                            else -> HabitType.VALUE
                        }
                        repository.addHabit(
                            name = habitName,
                            type = type,
                            description = habitDescription
                        )
                        onHabitAdded()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = "Agregar habito",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddHabitScreenPreview() {
    OneDayBetterTheme {
        AddHabitScreen(
            onHabitAdded = {},
            onNavigateToHome = {},
            onNavigateToHabits = {},
            onNavigateToGoals = {}
        )
    }
}