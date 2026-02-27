package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.pickMarkdownFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MarkdownLoaderButton(
    onContentLoaded: (String) -> Unit, // 1. Nuevo parámetro de lambda
    modifier: Modifier = Modifier, // 2. Nuevo parámetro Modifier con valor por defecto
    focusRequester: FocusRequester
) {
    // Estado para mostrar mensajes de estado al usuario (la lógica de carga permanece aquí)
    var statusMessage by remember { mutableStateOf("Esperando acción...") }
    val scope = rememberCoroutineScope()

    Button(

        onClick = {
            scope.launch(Dispatchers.IO) {
                statusMessage = "Abriendo selector..."
                // NOTA: 'openMarkdownFileDialog()' debe ser una función disponible en este alcance
                val content = pickMarkdownFile()

                if (content != null) {
                    // Llama a la lambda proporcionada por el invocador (en el hilo principal)
                    launch(Dispatchers.Main) {
                        onContentLoaded(content)
                        statusMessage = "¡Archivo cargado (${content.length} caracteres)!"
                        focusRequester.requestFocus()
                    }
                } else {
                    launch(Dispatchers.Main) {
                        statusMessage = "Carga cancelada o fallida."
                        focusRequester.requestFocus()
                    }
                }
            }
        },
        modifier = modifier // 3. Aplicamos el Modifier recibido al componente Button
    ) {
        Text("Cargar(.md)")
    }

    // Mostrar el estado de la operación (opcional, para feedback inmediato al usuario)
    //Text(statusMessage)
}