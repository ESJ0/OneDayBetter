package com.example.onedaybetter.ui.login

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onedaybetter.data.DataRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isRegisterMode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = if (isDark) Color(0xFF000000) else Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            Text(
                text = "OneDayBetter",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
            )

            Spacer(Modifier.height(80.dp))

            Text(
                text = if (isRegisterMode) "Crear cuenta" else "Iniciar sesión",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.White else Color.Black
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (isRegisterMode)
                    "Ingresa tus datos para registrarte"
                else
                    "Ingresa tus credenciales",
                fontSize = 14.sp,
                color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(24.dp))

            if (isRegisterMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    placeholder = {
                        Text(
                            "Nombre",
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
                    ),
                    enabled = !isLoading
                )

                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                placeholder = {
                    Text(
                        "email@domain.com",
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
                ),
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = {
                    Text(
                        "Contraseña",
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
                ),
                enabled = !isLoading,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = if (isDark) Color.Gray else Color.Gray
                        )
                    }
                }
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isRegisterMode) {
                        if (name.isBlank()) {
                            errorMessage = "El nombre es requerido"
                            return@Button
                        }
                        if (!email.contains("@")) {
                            errorMessage = "Email inválido"
                            return@Button
                        }
                        if (password.length < 6) {
                            errorMessage = "La contraseña debe tener al menos 6 caracteres"
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            val success = repository.registerUser(email, password, name)
                            if (success) {
                                val loginSuccess = repository.loginUser(email, password)
                                if (loginSuccess) {
                                    onLoginSuccess()
                                }
                            } else {
                                errorMessage = "El email ya está registrado"
                            }
                            isLoading = false
                        }
                    } else {
                        if (!email.contains("@")) {
                            errorMessage = "Email inválido"
                            return@Button
                        }
                        if (password.isBlank()) {
                            errorMessage = "La contraseña es requerida"
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            val success = repository.loginUser(email, password)
                            if (success) {
                                onLoginSuccess()
                            } else {
                                errorMessage = "Credenciales incorrectas"
                            }
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color.White else Color.Black
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = if (isDark) Color.Black else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (isRegisterMode) "Crear cuenta" else "Iniciar sesión",
                        fontSize = 16.sp,
                        color = if (isDark) Color.Black else Color.White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = {
                    isRegisterMode = !isRegisterMode
                    errorMessage = null
                },
                enabled = !isLoading
            ) {
                Text(
                    text = if (isRegisterMode)
                        "¿Ya tienes cuenta? Inicia sesión"
                    else
                        "¿No tienes cuenta? Regístrate",
                    fontSize = 14.sp,
                    color = if (isDark) Color.White else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "o",
                fontSize = 14.sp,
                color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f)
            )

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = { /* Google Sign In - To implement */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF5F5F5)
                ),
                enabled = !isLoading
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "G",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4285F4)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Continuar con Google",
                        fontSize = 16.sp,
                        color = if (isDark) Color.White else Color.Black
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { /* Apple Sign In - To implement */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF5F5F5)
                ),
                enabled = !isLoading
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Continuar con Apple",
                        fontSize = 16.sp,
                        color = if (isDark) Color.White else Color.Black
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Al continuar, aceptas nuestros ",
                        fontSize = 12.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Términos de Servicio",
                        fontSize = 12.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                        textDecoration = TextDecoration.Underline
                    )
                }
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "y ",
                        fontSize = 12.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Política de Privacidad",
                        fontSize = 12.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}