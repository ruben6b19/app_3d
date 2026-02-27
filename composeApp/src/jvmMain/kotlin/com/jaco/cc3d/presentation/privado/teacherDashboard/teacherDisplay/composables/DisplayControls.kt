package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.automirrored.filled.StopScreenShare
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.presentation.composables.buttons.DangerButton
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.TeacherDisplayViewModel
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models.DisplayState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.DisplayControlStrings
import kotlinx.coroutines.launch
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.showCustomWindowState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayControls(
    modifier: Modifier = Modifier,
    visible: Boolean,
    subjectName: String,
    currentSlideIndex: Int,
    totalSlides: Int,
    viewMode: DisplayState,
    isShowingScreens: Boolean,
    selectedBibleId: String,
    availableBibles: Collection<String>,
    showBibleSidebar: Boolean,
    viewModel: TeacherDisplayViewModel,
    // Acciones (Callbacks)
    onBack: () -> Unit,
    onNavigateSlide: (Int) -> Unit,
    onChangeViewMode: () -> Unit,
    onToggleScreens: () -> Unit,
    onSelectBible: (String) -> Unit,
    onToggleBibleSidebar: () -> Unit,
    onChangeFontSize: (Float) -> Unit,
    onMarkdownLoaded: (String) -> Unit,
    focusRequester: FocusRequester,
    texts: DisplayControlStrings,
    onOpenExam: () -> Unit,
    visualCurrentIndex: Int,
    visualTotalCount: Int,
    jumpToMasterSlide: (Int) -> Unit,
) {
    var showBibleMenu by remember { mutableStateOf(false) }
    val showCustomWindow by showCustomWindowState

    val scope = rememberCoroutineScope()

    var pageInput by remember(visualCurrentIndex) { mutableStateOf(visualCurrentIndex.toString()) }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .wrapContentWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
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
                // --- GRUPO 1: Navegación ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$subjectName\n${viewModel.getLocalIPAddress() ?: "Sin Ip"}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    DangerButton(
                        onClick = {
                            scope.launch {
                                // 1. Detenemos el servidor
                                viewModel.stopWebsocketServer()
                                // 2. Ejecutamos la navegación hacia atrás
                                onBack()
                            }
                        },
                        text = texts.backButton, isOutlined = true,
                        icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) })

                    VerticalDivider(Modifier.height(28.dp).padding(horizontal = 8.dp))

                    PrimaryButton(
                        onClick = {
                            onNavigateSlide(currentSlideIndex - 1)
                            focusRequester.requestFocus()
                                  },
                        enabled = currentSlideIndex > 0,
                        text = "",
                        icon = { Icon(Icons.AutoMirrored.Filled.NavigateBefore, "Anterior") }
                    )
                    // --- CAMPO DE TEXTO PARA NAVEGACIÓN DIRECTA ---
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        OutlinedTextField(
                            value = pageInput,
                            onValueChange = { newValue ->
                                // Solo permitir números
                                if (newValue.all { it.isDigit() }) pageInput = newValue
                            },
                            modifier = Modifier
                                .width(65.dp) // Ancho pequeño para que no rompa el diseño
                                .onKeyEvent { keyEvent ->
                                    // Detectar presión de la tecla ENTER
                                    if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                                        val targetPage = pageInput.toIntOrNull()
                                        if (targetPage != null && targetPage in 1..visualTotalCount) {
                                            jumpToMasterSlide(targetPage) // Convertir a 0-index
                                            focusRequester.requestFocus()
                                        } else {
                                            // Si es inválido, revertir al valor actual
                                            pageInput = visualCurrentIndex.toString()
                                        }
                                        true
                                    } else false
                                },
                            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )

                        Text(
                            text = "/ $visualTotalCount",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    PrimaryButton(
                        onClick = {
                            onNavigateSlide(currentSlideIndex + 1)
                            focusRequester.requestFocus()
                                  },
                        enabled = currentSlideIndex < totalSlides - 1,
                        text = "",
                        icon = { Icon(Icons.AutoMirrored.Filled.NavigateNext, "Siguiente") }
                    )
                }

                VerticalDivider(Modifier.height(32.dp).width(2.dp))

                val (buttonModeText, buttonIcon) = when (viewMode) {
                    DisplayState.CONTROLS -> texts.modeNotes to Icons.AutoMirrored.Filled.MenuBook
                    DisplayState.NOTES -> texts.modePresentation to Icons.Default.Fullscreen
                    DisplayState.PRESENTATION -> texts.modeControls to Icons.Default.ZoomOutMap
                }
                // --- GRUPO 2: Modos y Pantalla ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PrimaryButton(
                        onClick = {
                            onChangeViewMode()
                            focusRequester.requestFocus()
                        },
                        text = buttonModeText, // O una función que devuelva el texto amigable
                        isOutlined = true,
                        icon = { Icon(buttonIcon, null, Modifier.size(18.dp)) }
                    )

                    IconButton(onClick = {
                        onToggleScreens()
                        focusRequester.requestFocus()
                    }) {
                        Icon(
                            imageVector = if (isShowingScreens) Icons.AutoMirrored.Filled.ScreenShare else Icons.AutoMirrored.Filled.StopScreenShare,
                            contentDescription = null,
                            tint = if (isShowingScreens) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    val tooltipState = rememberTooltipState()

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            // 2. El diseño del cartelito que flota
                            PlainTooltip {
                                Text(texts.examTooltip) // Usamos la traducción: "Abrir Examen"
                            }
                        },
                        state = tooltipState
                    ) {
                        // 3. El botón que activa el tooltip
                        IconButton(onClick = {
                            onOpenExam()
                            focusRequester.requestFocus()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Assignment,
                                contentDescription = texts.examTooltip,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    IconButton(onClick = {
                        showCustomWindowState.value = !showCustomWindowState.value
                    }) {
                        Icon(
                            imageVector = Icons.Default.WebAsset, // Un icono de ventana
                            contentDescription = "Toggle Ventana Vacía",
                            tint = if (showCustomWindow) MaterialTheme.colorScheme.primary else Color.Black
                        )
                    }
                }

                VerticalDivider(Modifier.height(32.dp).width(2.dp))

                // --- GRUPO 3: Biblia y Configuración ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        PrimaryButton(
                            onClick = {
                                showBibleMenu = true
                                focusRequester.requestFocus()
                                      },
                            text = selectedBibleId,
                            isOutlined = true,
                            icon = { Icon(Icons.Default.Book, texts.bibleMenu, Modifier.size(18.dp)) }
                        )
                        DropdownMenu(expanded = showBibleMenu, onDismissRequest = { showBibleMenu = false }) {
                            availableBibles.forEach { id ->
                                DropdownMenuItem(text = { Text(id) }, onClick = {
                                    onSelectBible(id)
                                    showBibleMenu = false
                                })
                            }
                        }
                    }

                    IconButton(onClick = {
                        onToggleBibleSidebar()
                        focusRequester.requestFocus()
                    }) {
                        Icon(
                            imageVector = if (showBibleSidebar) Icons.Default.AutoStories else Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = if (showBibleSidebar) MaterialTheme.colorScheme.primary else Color.Black
                        )
                    }

                    Row(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            onChangeFontSize(-0.1f)
                            focusRequester.requestFocus()
                        }) { Icon(Icons.Default.Remove, null) }
                        IconButton(onClick = {
                            onChangeFontSize(0.1f)
                            focusRequester.requestFocus()
                        }) { Icon(Icons.Default.Add, null) }
                    }

                    VerticalDivider(Modifier.height(28.dp).padding(horizontal = 8.dp))

                    WebSocketControls(viewModel, focusRequester, texts.syncStart,texts.syncStop)
                    Spacer(modifier = Modifier.width(12.dp))
                    MarkdownLoaderButton(onContentLoaded = onMarkdownLoaded, focusRequester=focusRequester)
                }
            }
        }
    }
}