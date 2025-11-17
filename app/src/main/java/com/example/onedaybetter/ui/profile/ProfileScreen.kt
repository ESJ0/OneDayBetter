package com.example.onedaybetter.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var habitsCount by remember { mutableStateOf(0) }
    var goalsCount by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            val user = repository.getCurrentUser()
            userName = user?.name ?: ""
            userEmail = user?.email ?: ""
            habitsCount = repository.getHabitsCount()
            goalsCount = repository.getGoalsCount()
        }
    }

    Scaffold(
        containerColor = if (isDark) Color(0xFF000000) else Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF000000) else Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        if (isDark) Color(0xFF2C2C2E) else Color(0xFFF5F5F5),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(60.dp),
                    tint = if (isDark) Color.White else Color.Black
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = userName.ifBlank { "Usuario" },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = userEmail,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(32.dp))

            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = "Hábitos",
                    value = habitsCount.toString(),
                    isDark = isDark
                )
                StatCard(
                    title = "Metas",
                    value = goalsCount.toString(),
                    isDark = isDark
                )
            }

            Spacer(Modifier.height(32.dp))

            // Información de la cuenta
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isDark) Color(0xFF1C1C1E) else Color(0xFFF8F8F8),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Información de la cuenta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color.Black
                )

                Spacer(Modifier.height(16.dp))

                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Nombre",
                    value = userName.ifBlank { "Sin nombre" },
                    isDark = isDark
                )

                Spacer(Modifier.height(12.dp))

                InfoRow(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = userEmail,
                    isDark = isDark
                )
            }

            Spacer(Modifier.height(32.dp))

            // Botón de cerrar sesión
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC3545)
                )
            ) {
                Text(
                    text = "Cerrar sesión",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.logoutUser()
                            onLogout()
                        }
                    }
                ) {
                    Text("Sí, cerrar sesión", color = Color(0xFFDC3545))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    isDark: Boolean
) {
    Column(
        modifier = Modifier
            .background(
                if (isDark) Color(0xFF1C1C1E) else Color(0xFFF8F8F8),
                RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color.Black
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isDark: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = if (isDark) Color.White else Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}