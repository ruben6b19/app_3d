package com.jaco.cc3d.presentation.composables

// Archivo: SidebarPanel.kt (o similar)

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.ArrowDropDown
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.LocalThemeActions // Importa las acciones del tema
import com.jaco.cc3d.LocalSidebarActions // Importa las acciones del panel
import com.jaco.cc3d.presentation.composables.util.SidebarResources

val availableLanguages = mapOf(
    "es" to "Español",
    "en" to "English",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SidebarPanel() {
    val themeActions = LocalThemeActions.current
    val sidebarActions = LocalSidebarActions.current
    val languageActions = LocalLanguageActions.current

    // 💡 2. OBTENER EL IDIOMA ACTUAL DE LAS ACCIONES GLOBALES
    val currentLanguageCode = languageActions.currentLanguage
    val strings = SidebarResources.get(currentLanguageCode)

    var expanded by remember { mutableStateOf(false) }
    //var selectedLanguageCode by remember { mutableStateOf("es") } // Simular el idioma actual

    // Simulación de la función de cambio de idioma
    //val onLanguageChange: (String) -> Unit = { newCode ->
    //    selectedLanguageCode = newCode
    //}
    // Usar Surface para que el fondo del panel se adapte al tema
    Surface(
        modifier = Modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.background, // Usa 'background' o 'surface'
        shadowElevation = 8.dp // Da un efecto de elevación al panel
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxHeight()
        ) {
            // Título/Cerrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(strings.title, style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = sidebarActions::toggleSidebar) {
                    Icon(Icons.Default.Close, contentDescription = strings.closeDescription)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Control de Tema
            Text(strings.screenModeTitle, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (themeActions.isDark) strings.darkMode else strings.lightMode)
                Switch(
                    checked = themeActions.isDark,
                    onCheckedChange = { themeActions.toggleTheme() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Control de Idioma (Dropdown)
            Text(strings.languageTitle, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Usamos ExposedDropdownMenuBox para el control "Spinner"
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Campo de texto que muestra la selección actual
                OutlinedTextField(
                    // 💡 Usa el código de idioma global para mostrar el nombre del idioma
                    value = availableLanguages[currentLanguageCode] ?: strings.selectLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(strings.languageLabel) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                // Menú desplegable con las opciones
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    availableLanguages.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                // 💡 3. LLAMAR A LA FUNCIÓN GLOBAL PARA CAMBIAR EL IDIOMA
                                languageActions.setLanguage(code)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            // Aquí puedes añadir más controles (idioma, etc.)
        }
    }
}