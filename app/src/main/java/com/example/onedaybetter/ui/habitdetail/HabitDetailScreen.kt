package com.example.onedaybetter.ui.habitdetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.DataRepository
import com.example.onedaybetter.data.Habit
import com.example.onedaybetter.data.HabitType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: Int,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    var habit by remember { mutableStateOf<Habit?>(null) }

    LaunchedEffect(habitId) {
        scope.launch {
            repository.getAllHabits().collect { allHabits ->
                habit = allHabits.find { it.id == habitId }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del hábito") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        habit?.let { h ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(32.dp))

                // Círculo de porcentaje
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(200.dp)
                ) {
                    CircularProgressIndicator(
                        h.getCompletionPercentage(),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.height(48.dp))

                // Nombre del hábito
                Text(
                    text = h.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(16.dp))

                // Tipo de hábito
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = h.type.getIconVector(),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = h.type.getDisplayName(),
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Descripción
                if (h.description.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = h.description,
                            fontSize = 16.sp,
                            color = Color.Black,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Días activos
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Días activos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("L", "M", "X", "J", "V", "S", "D").forEachIndexed { index, day ->
                            val isActive = h.daysOfWeek.contains(index + 1)
                            Text(
                                text = day,
                                fontSize = 16.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActive) Color.Black else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 20f

            // Fondo
            drawArc(
                color = Color(0xFFE0E0E0),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progreso
            drawArc(
                color = Color.Black,
                startAngle = -90f,
                sweepAngle = (percentage / 100f) * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "$percentage%",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

fun HabitType.getDisplayName(): String {
    return when(this) {
        HabitType.EXERCISE -> "Ejercicio"
        HabitType.SLEEP -> "Sueño"
        HabitType.FOOD -> "Alimentación"
        HabitType.VALUE -> "Valor"
    }
}

fun HabitType.getIconVector(): androidx.compose.ui.graphics.vector.ImageVector {
    return when(this) {
        HabitType.EXERCISE -> androidx.compose.material.icons.Icons.Default.DirectionsRun
        HabitType.SLEEP -> androidx.compose.material.icons.Icons.Default.Bedtime
        HabitType.FOOD -> androidx.compose.material.icons.Icons.Default.Restaurant
        HabitType.VALUE -> androidx.compose.material.icons.Icons.Default.Star
    }
}