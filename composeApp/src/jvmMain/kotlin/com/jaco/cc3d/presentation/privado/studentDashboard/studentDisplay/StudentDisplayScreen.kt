package com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.* // Importamos todo Material3 para usar Surface, etc.
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.di.Injector.bibleFileManager
import com.jaco.cc3d.presentation.composables.ScreenLayout
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.composables.StudentDisplayControls
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables.SlideView
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.currentBibleIdState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.currentBibleFontSizeState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.DisplayResources
import androidx.compose.ui.input.key.*
import com.jaco.cc3d.common.AppConfig
import com.jaco.cc3d.presentation.composables.LoadingOverlay
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.composables.QuizDialog
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.composables.QuizSelectionDialog
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.util.QuizDataProvider
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.util.StudentDisplayResources
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.bibleScrollValueState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.handleVerseHover
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.showBibleSidebarState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.extractVerseCitations
import kotlinx.coroutines.delay

// Asumo que esta clase existe para que el código compile


@Composable
fun StudentDisplayScreen(
    modifier: Modifier = Modifier,
    viewModel: StudentDisplayViewModel,
    courseId: String,
    subjectId: String,
    subjectName: String,
    contentUrl: String,
    onBack: () -> Unit,
    //onMenuClick: () -> Unit
) {

    // 1. ESTADOS DEL VIEWMODEL (RECOLECTADOS)
    val connectionState by viewModel.connectionState.collectAsState()
    val currentSlideNumber by viewModel.currentSlideNumber.collectAsState()
    val slides by viewModel.slides.collectAsState()
    val currentServerIp by viewModel.currentServerIp.collectAsState()
    val currentRetryCount by viewModel.currentRetryCount.collectAsState()
    val retryDelayMessage by viewModel.retryDelayMessage.collectAsState()
    val isSyncEnabled by viewModel.isManualConnectionEnabled.collectAsState()
    val quizQuestions by viewModel.quizQuestions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 2. RECURSOS Y TRADUCCIONES (REACTIVOS AL IDIOMA)
    val languageActions = LocalLanguageActions.current
    val currentLangCode = languageActions.currentLanguage
    val displayStrings = DisplayResources.get(currentLangCode)
    val strings = StudentDisplayResources.get(currentLangCode)
    val bibleTexts = displayStrings.bible

    // 3. LÓGICA DERIVADA (CALCULADA EN CADA RECOMPOSICIÓN)
    val currentIndex = currentSlideNumber ?: 0
    val totalSlides = slides.size
    val currentSlideContent = slides.getOrNull(currentIndex)
    val isConnected = connectionState is ConnectionState.Connected

    val connectionStatusText = when (val state = connectionState) {
        is ConnectionState.Disconnected -> strings.connection.statusDisconnected
        is ConnectionState.Searching -> strings.connection.statusSearching(state.host, state.port)
        is ConnectionState.Connected -> strings.connection.statusConnected(state.subjectName)
        is ConnectionState.Error -> strings.connection.statusError
        is ConnectionState.NotFound -> strings.connection.statusNotFound
    }

    // 4. ESTADOS LOCALES DE LA UI (UI STATE)
    var showConnectDialog by remember { mutableStateOf(false) }
    var ipInput by remember { mutableStateOf(currentServerIp) }
    var showControls by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    var showQuizDialog by remember { mutableStateOf(false) }
    var showQuizSelection by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }


    // 5. ESTADOS COMPARTIDOS/GLOBALES (SINGLETONS O MANAGER)
    val currentBibleFontSize by remember { currentBibleFontSizeState }
    val selectedBibleId by remember { currentBibleIdState }
    val globalShowBible by remember { showBibleSidebarState }
    val availableBibles = remember { bibleFileManager.getAvailableBibleIds() }
    //val availableQuizzes = remember { listOf("Examen Parcial 1", "Simulacro de Repaso") } // Mock

    val availableQuizzes by viewModel.availableQuizzes.collectAsState()
    val hasQuizzes by viewModel.hasPendingQuizzes.collectAsState()

    var isBibleSidebarVisible by remember { mutableStateOf(false) }

    val fetchVerseCallback: suspend (String, String) -> String = remember(viewModel) {
        { bibleId, citation -> viewModel.fetchVerseContent(bibleId, citation) }
    }

    val sampleQuestions = remember { QuizDataProvider.getSampleQuestions() }

    // Sincroniza el idioma con el de la aplicación
    LaunchedEffect(contentUrl) {
        if (contentUrl.isNotEmpty()) {
            viewModel.loadMarkdownFromCloudinary(subjectId,contentUrl)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorEvents.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Cerrar",
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(currentLangCode) {
        viewModel.lang = currentLangCode
        //bibleTextsState.value = displayStrings.bible
    }

    LaunchedEffect(showConnectDialog) {
        if (!showConnectDialog) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(courseId) {
        // viewModel.loadCourseData(courseId)
        println("md: "+contentUrl)
        viewModel.initialize(courseId, subjectName)
    }

    LaunchedEffect(Unit) {
        try {
            // Esperamos un momento corto (100ms) para que el nodo se "adjunte"
            delay(100)
            focusRequester.requestFocus()
        } catch (e: Exception) {
            // Si falla porque el usuario salió muy rápido, no crashea la app
            println("No se pudo obtener el foco: ${e.message}")
        }
    }

    DisposableEffect(viewModel) {
        onDispose {
            // Llama a la función de cancelación del scope que definiste en el ViewModel
            viewModel.onCleared()
        }
    }

    // UI simple para mostrar el estado y la diapositiva
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    snackbarData = data
                )
            }
        }
    ) { paddingValues ->
        ScreenLayout(title = "Pantalla de Presentación") {
            Box(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFF222222)) // Fondo oscuro
                    .focusRequester(focusRequester)
                    .focusable()
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                        when (event.key) {
                            // Navegación de Diapositivas
                            Key.DirectionRight -> {
                                viewModel.nextSlide(); true
                            }

                            Key.DirectionLeft -> {
                                viewModel.previousSlide(); true
                            }

                            // Control de Biblia
                            Key.A -> {
                                showBibleSidebarState.value = false
                                handleVerseHover(null)
                                true
                            }

                            Key.S -> {
                                showControls = !showControls
                                true
                            }

                            Key.C -> {
                                viewModel.toggleConnection(); true
                            }

                            Key.DirectionUp -> {
                                if (globalShowBible) {
                                    bibleScrollValueState.value = (bibleScrollValueState.value - 50).coerceAtLeast(0)
                                    true
                                } else false
                            }

                            Key.DirectionDown -> {
                                if (globalShowBible) {
                                    bibleScrollValueState.value += 50
                                    true
                                } else false
                            }

                            // Salir / Atrás
                            Key.Escape -> {
                                onBack(); true
                            }

                            // Teclas Rápidas Q, W, E, R...
                            else -> {
                                val keyIndex = AppConfig.verseKeys.indexOf(event.key)
                                // Asumiendo que SlideContent tiene una propiedad 'citations' o similar
                                //val citations = currentSlideContent?.citations ?: emptyList()
                                val citations =
                                    currentSlideContent?.contentText?.let { extractVerseCitations(it) } ?: emptyList()
                                if (keyIndex != -1 && keyIndex < citations.size) {
                                    handleVerseHover(citations[keyIndex])
                                    true
                                } else false
                            }
                        }
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusRequester.requestFocus()
                    },
                contentAlignment = Alignment.Center
            ) {

                // 1. Contenido principal (SlideView)
                // Ya no está envuelto en Column, ocupa el 100% del Box.
                SlideView(
                    slide = currentSlideContent,
                    onFetchVerseContent = fetchVerseCallback,
                    //showBibleSidebar = isBibleSidebarVisible,
                    currentBibleFontSize = currentBibleFontSize,
                    selectedBibleId = selectedBibleId,
                    texts = bibleTexts
                )

                IconButton(
                    onClick = {
                        showControls = !showControls
                        focusRequester.requestFocus()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (showControls) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Alternar Controles",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                AnimatedVisibility(
                    visible = showControls,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    StudentDisplayControls(
                        currentSlideIndex = currentIndex,
                        totalSlides = totalSlides,
                        currentServerIp = currentServerIp,
                        selectedBibleId = selectedBibleId,
                        isBibleVisible = isBibleSidebarVisible,
                        onNavigate = { index ->
                            viewModel.goToSlide(index)
                            // Es buena práctica pedir el foco después de navegar
                            try {
                                focusRequester.requestFocus()
                            } catch (e: Exception) {
                                println("No se pudo recuperar el foco tras navegar")
                            }
                        },
                        availableBibles = availableBibles,
                        onBack = onBack,
                        onShowConnect = { showConnectDialog = true },
                        onToggleBible = {
                            if (globalShowBible) handleVerseHover(null) else handleVerseHover("")
                            focusRequester.requestFocus()
                        },
                        onSelectBible = { id ->
                            currentBibleIdState.value = id
                            handleVerseHover("")
                            focusRequester.requestFocus()
                        },
                        isConnected = isConnected,
                        onToggleConnection = { viewModel.toggleConnection() },
                        controlStrings = strings.controls,
                        connectionStrings = strings.connection,
                        onStartQuiz = {
                            viewModel.loadScheduledQuizzes(courseId)
                            showQuizSelection = true
                        },
                        focusRequester = focusRequester,
                        visualCurrentIndex = slides.getOrNull(currentSlideNumber)?.masterSlideIndex ?: 1,
                        visualTotalCount = slides.lastOrNull()?.masterSlideIndex ?: 1,
                        onJumpToMaster = { page ->
                            viewModel.jumpToMasterSlide(page)
                        },
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = connectionStatusText,
                        style = MaterialTheme.typography.labelLarge,
                        //color = if (currentSlideNumber != null) Color.Green else Color.Red,
                        color = when (connectionState) {
                            is ConnectionState.Connected -> Color(0xFF00C853) // Verde
                            is ConnectionState.Error, ConnectionState.NotFound -> MaterialTheme.colorScheme.error // Rojo
                            is ConnectionState.Searching -> Color.Yellow // Opcional: un color para la búsqueda
                            else -> Color.Gray
                        },
                        modifier = Modifier
                            //.align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                            .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    // ⭐️ LÍNEA 2: CONTADOR/DELAY DE RECONEXIÓN (NUEVO) ⭐️
                    if (retryDelayMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⏳ $retryDelayMessage",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(
                                    Color.DarkGray.copy(alpha = 0.9f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    if (currentRetryCount > 0) {
                        // Muestra el contador mientras está en el intento activo
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🔄 ${strings.connection.retryAttempt(currentRetryCount, MAX_RETRY_COUNT)}",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(
                                    Color.DarkGray.copy(alpha = 0.9f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                if (showConnectDialog) {
                    AlertDialog(
                        onDismissRequest = { showConnectDialog = false },
                        title = { Text(strings.connection.dialogTitle) },
                        text = {
                            Column {
                                Text(strings.connection.dialogDesc)
                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(
                                    // Asegúrate de inicializar el input con la IP actual del VM
                                    value = ipInput,
                                    onValueChange = { ipInput = it },
                                    label = { Text(strings.connection.ipAddressLabel) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    // 🔑 LLAMADA AL VIEWMODEL: Actualiza la IP
                                    viewModel.setServerIp(ipInput.substringBefore(":"))
                                    // Cierra el diálogo
                                    showConnectDialog = false
                                }
                            ) {
                                Text(strings.connection.connect)
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showConnectDialog = false }) {
                                Text(strings.controls.backButton)
                            }
                        }
                    )
                }
                if (showQuizSelection) {
                    QuizSelectionDialog(
                        availableQuizzes = availableQuizzes,
                        strings = strings.quizzes,
                        onDismiss = { showQuizSelection = false },
                        onQuizSelected = { selectedQuiz ->
                            showQuizSelection = false
                            //viewModel.loadQuizQuestions(selectedQuiz.quizTemplateId)
                            viewModel.sendMessageToTeacher("STUDENT_START_QUIZ:${selectedQuiz.id}")
                            viewModel.startQuiz(
                                scheduledQuizId = selectedQuiz.id,
                                // studentId = "ID_DEL_ESTUDIANTE_LOGUEADO"
                            )
                            showQuizDialog = true // Abrimos el examen real
                        }
                    )
                }

                if (showQuizDialog) {
                    if (viewModel.isLoadingQuestions) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        QuizDialog(
                            questions = quizQuestions, // <--- Aquí ya son las preguntas de la BD
                            strings = strings.quizzes,
                            onDismiss = {
                                showQuizDialog = false
                                viewModel.clearQuestions() // Tip: limpia las preguntas al cerrar
                            },
                            onSubmit = { answers ->
                                viewModel.submitQuiz(answers)
                                // Aquí enviarías las respuestas al backend
                                showQuizDialog = false
                            }
                        )
                    }
                }
                LoadingOverlay(isLoading = isLoading)
            }
        }
    }
}

// Importante: Asegúrate de tener importado java.util.Date
