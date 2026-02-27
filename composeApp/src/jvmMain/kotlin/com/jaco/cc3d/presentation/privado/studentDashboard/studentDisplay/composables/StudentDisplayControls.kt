package com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaco.cc3d.presentation.composables.buttons.DangerButton
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton
import com.jaco.cc3d.presentation.composables.buttons.StatusButton
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.util.ConnectionStrings
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.util.StudentControlStrings
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign


@Composable
fun StudentDisplayControls(
    currentSlideIndex: Int,
    totalSlides: Int,
    currentServerIp: String,
    onNavigate: (Int) -> Unit,
    selectedBibleId: String,
    isBibleVisible: Boolean,
    availableBibles: Collection<String>,
    onBack: () -> Unit,
    onShowConnect: () -> Unit,
    onToggleBible: () -> Unit,
    onSelectBible: (String) -> Unit,
    isConnected: Boolean,
    onToggleConnection: () -> Unit,
    controlStrings: StudentControlStrings,    // 👈 Nuevo
    connectionStrings: ConnectionStrings,
    onStartQuiz: () -> Unit,
    focusRequester: FocusRequester,
    //currentMasterIndex: Int,   // Página visual (ej: 14)
    //totalMasterSlides: Int,
    visualCurrentIndex: Int,
    visualTotalCount: Int,
    onJumpToMaster: (Int) -> Unit
) {
    var showBibleMenu by remember { mutableStateOf(false) }
    var pageInput by remember(visualCurrentIndex) { mutableStateOf(visualCurrentIndex.toString()) }
    //var pageInput by remember(visualCurrentIndex) { mutableStateOf(visualCurrentIndex.toString()) }


    Surface(
        modifier = Modifier
            .padding(bottom = 24.dp) // Espacio inferior para que flote
            .wrapContentWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp), // Estética redondeada tipo cápsula
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 12.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- GRUPO 1: NAVEGACIÓN ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Botón Salir (Ahora a la izquierda como en el profesor)
                DangerButton(
                    onClick = onBack,
                    text = controlStrings.backButton,
                    isOutlined = true,
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                val isManualEnabled = true // Esto vendría de tu ViewModel
                val (btnColor, txtColor) = when {
                    !isManualEnabled -> {
                        // Estado: Switch OFF (Gris/Apagado)
                        MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    isConnected -> {
                        // Estado: Conectado y Sincronizado (Verde)
                        Color(0xFF2E7D32) to Color.White
                    }
                    else -> {
                        // Estado: Intentando conectar/Error (Naranja o Rojo suave)
                        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
                    }
                }

                StatusButton(
                    onClick ={
                        onToggleConnection()
                        focusRequester.requestFocus()
                    } ,
                    containerColor = btnColor,
                    contentColor = txtColor,
                    text = currentServerIp.ifEmpty { connectionStrings.setIp },
                    icon = {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.Sync else Icons.Default.SyncDisabled,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                VerticalDivider(Modifier.height(28.dp).padding(horizontal = 12.dp))

                PrimaryButton(
                    onClick = {
                        //onPrevious()
                        onNavigate(currentSlideIndex - 1)
                        focusRequester.requestFocus() // 🎯 Devolver foco
                    },
                    enabled = currentSlideIndex > 0,
                    text = "",
                    icon = { Icon(Icons.AutoMirrored.Filled.NavigateBefore, controlStrings.prevSlide) }
                )

                // 2. CAMPO DE TEXTO PARA NAVEGACIÓN DINÁMICA
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    OutlinedTextField(
                        value = pageInput,
                        onValueChange = { if (it.all { char -> char.isDigit() }) pageInput = it },
                        modifier = Modifier
                            .width(60.dp)
                            .onKeyEvent { keyEvent ->
                                if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                                    val targetPage = pageInput.toIntOrNull()
                                    if (targetPage != null && targetPage in 1..visualTotalCount) {
                                        onJumpToMaster(targetPage) // Convertir a 0-index
                                        focusRequester.requestFocus()
                                    } else {
                                        // Si es inválido, revertir al valor actual
                                        pageInput = visualCurrentIndex.toString()
                                    }
                                    true
                                } else false
                            },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    )

                    Text(
                        text = " / $visualTotalCount",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }


                PrimaryButton(
                    onClick = {
                        //onNext()
                        onNavigate(currentSlideIndex + 1)
                        focusRequester.requestFocus() // 🎯 Devolver foco
                    },
                    enabled = currentSlideIndex < totalSlides - 1,
                    text = "",
                    icon = { Icon(Icons.AutoMirrored.Filled.NavigateNext, controlStrings.nextSlide) }
                )
            }

            VerticalDivider(Modifier.height(32.dp).width(2.dp))

            // --- GRUPO 2: BIBLIA Y CONEXIÓN ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Selector de Biblia
                IconButton(
                    onClick = {
                        onStartQuiz()
                        focusRequester.requestFocus() // 🎯 Devolver foco
                    },
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                        CircleShape
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Quiz, // O Icons.Default.Assignment
                        contentDescription = "Ver Exámenes",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                Box {
                    PrimaryButton(
                        onClick = {
                            showBibleMenu = true
                            focusRequester.requestFocus() // 🎯 Devolver foco
                        },
                        text = selectedBibleId,
                        isOutlined = true,
                        icon = { Icon(Icons.Default.Book, null, Modifier.size(18.dp)) }
                    )

                    DropdownMenu(
                        expanded = showBibleMenu,
                        onDismissRequest = { showBibleMenu = false }
                    ) {
                        availableBibles.forEach { id ->
                            DropdownMenuItem(
                                text = { Text(id) },
                                onClick = {
                                    onSelectBible(id)
                                    showBibleMenu = false
                                    focusRequester.requestFocus()
                                },
                                trailingIcon = {
                                    if (id == selectedBibleId) Icon(Icons.Default.Check, null)
                                }
                            )
                        }
                    }
                }

                // Botón para mostrar/ocultar biblia (Icono)
                IconButton(onClick = {
                    onToggleBible()
                    focusRequester.requestFocus() // 🎯 Devolver foco
                }) {
                    Icon(
                        imageVector = if (isBibleVisible) Icons.Default.AutoStories else Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "Toggle Biblia",
                        tint = if (isBibleVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                VerticalDivider(Modifier.height(28.dp).padding(horizontal = 8.dp))

                // Estado de Conexión / IP
                PrimaryButton(
                    onClick = {
                        onShowConnect()
                        focusRequester.requestFocus() // 🎯 Devolver foco
                    },
                    text = currentServerIp.ifEmpty {  connectionStrings.connect },
                    isOutlined = true,
                    icon = {
                        Icon(
                            if (currentServerIp.isEmpty()) Icons.Default.NetworkCheck else Icons.Default.NetworkWifi,
                            null,
                            Modifier.size(18.dp)
                        )
                    }
                )
            }
        }
    }
}