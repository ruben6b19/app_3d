package com.jaco.cc3d.presentation.publico.register

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text

// Archivo: LoginScreen.kt

@Composable
fun RegisterScreen(
    //navigateToRegister: () -> Unit,

    ) {
    // 🔑 OBTENER EL VIEWMODEL: Accede a cualquier VM que necesites
    // Aunque el VM de la Biblia no sea para el Login, este es el patrón:
    //val bibleViewModel: BibleViewModel = getViewModel()

    // 🔑 CONSUMIR ESTADO DEL VM
    //val statusText = bibleViewModel.verseResult

    Column {
        Text("Registro")
        Text("Estado del DAO: ") // Ejemplo de uso del VM
        Button(onClick = {}) {
            Text("Ir a Líneas")
        }
        // ... (UI de Login)
    }
}