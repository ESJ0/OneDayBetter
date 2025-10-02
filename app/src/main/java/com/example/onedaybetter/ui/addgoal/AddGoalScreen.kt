package com.example.onedaybetter.ui.addgoal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.GoalRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(onGoalAdded: () -> Unit, onNavigateToList: () -> Unit) {
    var goalName by remember { mutableStateOf("") }
    var goalDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("Calendar") }
    var expanded by remember { mutableStateOf(false) }
    val repository = GoalRepository.getInstance()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 1,
                onTabSelected = { tab ->
                    if (tab == 0) onNavigateToList()
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
                text = "Define tu meta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Meta",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = goalName,
                onValueChange = { goalName = it },
                placeholder = { Text("Nombre de la meta", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Fecha",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Box {
                OutlinedTextField(
                    value = selectedDate,
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
                    listOf("Hoy", "Mañana", "En una semana", "En un mes").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedDate = option
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
                value = goalDescription,
                onValueChange = { goalDescription = it },
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
                    if (goalName.isNotBlank()) {
                        repository.addGoal(
                            name = goalName,
                            description = goalDescription,
                            targetDate = "31/11/2025",
                            targetValue = "+85"
                        )
                        onGoalAdded()
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
                    text = "Agregar meta",
                    fontSize = 16.sp,
                    color = Color.White
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
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { onTabSelected(0) }) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_sort_by_size),
                contentDescription = "Lista",
                tint = if (selectedTab == 0) Color.Black else Color.Gray
            )
        }
        IconButton(onClick = { onTabSelected(1) }) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_save),
                contentDescription = "Guardados",
                tint = if (selectedTab == 1) Color.Black else Color.Gray
            )
        }
        IconButton(onClick = { onTabSelected(2) }) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_edit),
                contentDescription = "Editar",
                tint = if (selectedTab == 2) Color.Black else Color.Gray
            )
        }
    }
}