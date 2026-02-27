package com.jaco.cc3d.presentation.publico.login

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.LocalThemeActions
import com.jaco.cc3d.domain.models.UserRoles
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton

// Archivo: LoginScreen.kt

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navigateToTeacherDashboard: () -> Unit,
    navigateToStudentDisplay: () -> Unit,
    //navigateToUsers: () -> Unit,
    navigateToPrivateDashboard: () -> Unit,

    ) {
    // 🔑 CONSUMIR EL ESTADO DEL VM
    val state by viewModel.state.collectAsState()
    val themeActions = LocalThemeActions.current

    var username by remember { mutableStateOf("est22@mail.com") }
    //var username by remember { mutableStateOf("ruben6b19@gmail.com") }
    var password by remember { mutableStateOf("q1w2e3r4") }

    // 🔑 EFECTO SECUNDARIO: Navegación al completar el Login
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            val user = viewModel.getUserData() // Función que debes crear en el VM
            val roles = user?.role ?: emptyList()
            // 1. Intentamos obtener el último rol usado, si no, usamos el primero de la jerarquía
            val targetRole = viewModel.getLastRole() ?: when {
                roles.contains(UserRoles.ADMIN) -> UserRoles.ADMIN
                roles.contains(UserRoles.TEACHER) -> UserRoles.TEACHER
                roles.contains(UserRoles.STUDENT) -> UserRoles.STUDENT
                else -> null
            }

            // 2. Navegamos según el rol determinado
            when (targetRole) {
                UserRoles.ADMIN -> navigateToPrivateDashboard()
                UserRoles.TEACHER -> navigateToTeacherDashboard()
                UserRoles.STUDENT -> navigateToStudentDisplay()
                else -> println("Sin roles")
            }

            // 3. Opcional: Si es la primera vez (lastRole era null), guardamos el actual
            if (targetRole != null && viewModel.getLastRole() == null) {
                viewModel.saveLastRole(targetRole)
            }
        }
    }

    Surface(
        // 2. Aplica el color de fondo del tema
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize() // Asegura que Surface cubra toda la ventana
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            /*Button(
                onClick = themeActions::toggleTheme, // Llama a la función toggleTheme
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(if (themeActions.isDark) "Modo Claro" else "Modo Oscuro")
            }*/
            Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(32.dp))

            // Campo de Usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Correo electronico") },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(0.3f)
            )
            Spacer(Modifier.height(16.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(0.3f)
            )
            Spacer(Modifier.height(32.dp))

            // Botón de Login
            PrimaryButton(
                onClick = { viewModel.login(username, password) },
                enabled = !state.isLoading && username.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth(0.3f),
                isOutlined = true,
                text = "Ingresar"
            )
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
            }
            Spacer(Modifier.height(8.dp))
            /*Button(
                onClick = navigateToTeacherDashboard,
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {

                Text("profesor")

            }
            Button(
                onClick = navigateToStudentDisplay,
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Alumno")
            }
            // Botón de Registro
            TextButton(onClick = navigateToTeacherDashboard) {
                Text("¿No tienes cuenta? Regístrate")
            }*/

            // Mostrar Error
            state.error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }

}