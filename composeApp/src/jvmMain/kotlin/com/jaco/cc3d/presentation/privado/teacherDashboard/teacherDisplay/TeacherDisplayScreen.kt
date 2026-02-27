package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.sp
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.LocalSidebarActions
import com.jaco.cc3d.di.Injector
import com.jaco.cc3d.presentation.composables.ScreenLayout

import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.currentSlideState
import com.jaco.cc3d.presentation.composables.SlideThumbnailView
import com.jaco.cc3d.presentation.composables.SlideThumbnailViewWithoutContent
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables.DisplayControls
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables.ExamSelectorDialog
import com.jaco.cc3d.presentation.composables.LoadingOverlay
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables.CustomEmptyOverlay
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables.VerseDisplayArea
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.bibleCloseJob
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.bibleTextsState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.connectedUsersState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.controlsVisibleState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.currentBibleIdState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.currentBibleFontSizeState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.currentVerseCitationState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.handleVerseHover
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.manageSecondaryDisplays
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.secondaryDisplayWindows
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.showBibleSidebarState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.currentSlideIndexState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.handleGlobalKeyEvent
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.showCustomWindowState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.showSecondaryWindowsState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.totalSlidesState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.versesInCurrentSlideState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models.DisplayState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.DisplayResources
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.extractVerseCitations
import kotlinx.coroutines.launch

private val CONTROLS_HEIGHT = 80.dp
private val HORIZONTAL_SCREEN_PADDING = 16.dp
private val VERTICAL_SCREEN_PADDING = 16.dp
private val PADDING = 16.dp
private val BUTTON_SPACING = 8.dp
private val MESSAGE_CORNER_PADDING = 8.dp
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TeacherDisplayScreen(
    viewModel: TeacherDisplayViewModel,
    courseId: String,
    subjectId: String,
    subjectName: String,
    contentUrl: String,
    onBack: () -> Unit,
    //onMenuClick: () -> Unit
    ) {
    val appBarTitle = "Presentación: $subjectName"

    val sidebarActions = LocalSidebarActions.current
    // 1. INYECTORES Y MANAGERS
    val bibleFileManager = remember { Injector.bibleFileManager }
    val scope = rememberCoroutineScope()
    val mainKeyboardActionAreaRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }

    // 2. RECURSOS DE IDIOMA Y TEXTOS
    val languageActions = LocalLanguageActions.current
    val currentLangCode = languageActions.currentLanguage
    val displayStrings = DisplayResources.get(currentLangCode)
    val controlTexts = displayStrings.controls
    val bibleTexts = displayStrings.bible
    val messageTexts = displayStrings.messages

    // 3. ESTADOS DE CONFIGURACIÓN Y UI
    var viewMode by remember { mutableStateOf(DisplayState.CONTROLS) }
    var showExamDialog by remember { mutableStateOf(false) }

    // 4. ESTADOS COMPARTIDOS (Provenientes de State Managers externos)
    val markdownText by viewModel.markdownContent.collectAsState()
    val message by viewModel.message.collectAsState()
    val showBibleSidebar by showBibleSidebarState
    val currentCitation by currentVerseCitationState
    var showSecondaryWindowsIntent by showSecondaryWindowsState
    val currentBibleFontSize by remember { currentBibleFontSizeState }
    var selectedBibleId by remember { currentBibleIdState }
    var currentSlideIndex by currentSlideIndexState
    var currentSlide by currentSlideState
    var controlsVisible by controlsVisibleState
    val showCustomWindow by showCustomWindowState

    // 5. CONSTANTES Y CONFIGURACIÓN DE VISTA
    val availableBibles = remember { bibleFileManager.getAvailableBibleIds() }
    val minFontSize = -0.3f
    val maxFontSize = 0.6f
    val fontSizeStep = 0.1f
    val fontSizeS = 11.sp
    val fontSizeN = 18.sp
    val fontSizeB = 37.sp
    //val slides: SnapshotStateList<SlideContent> = remember { mutableStateListOf() }
    val slides by viewModel.slides.collectAsState()
    val isParsing by viewModel.isParsing.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val connectedUsers by viewModel.currentUsers

    // 6. LÓGICA DE DIAPOSITIVAS (DERIVADA)
    val prevSlide = slides.getOrNull(currentSlideIndex - 1)
    val nextSlide = slides.getOrNull(currentSlideIndex + 1)
    val currentVisualIndex = slides.getOrNull(currentSlideIndex)?.masterSlideIndex ?: 0
    val totalVisualCount = slides.lastOrNull()?.masterSlideIndex ?: 0

    // 7. CALLBACKS Y FUNCIONES DE CONTROL
    val fetchVerseCallback: suspend (String, String) -> String = remember(viewModel) {
        { bibleId, citation -> viewModel.fetchVerseContent(bibleId, citation) }
    }

    val toggleScreens = {
        val newIntent = !showSecondaryWindowsIntent
        showSecondaryWindowsIntent = newIntent
        manageSecondaryDisplays(newIntent, fetchVerseCallback)
    }

    fun recoverFocus() {
        mainKeyboardActionAreaRequester.requestFocus()
    }

    val jumpToMasterSlide = { masterIndex: Int ->
        // Buscamos el primer índice en la lista de 'slides' que coincida con ese masterIndex
        val targetIndex = slides.indexOfFirst { it.masterSlideIndex == masterIndex }
        println("targetIndex-> "+masterIndex)
        println("targetIndex-> "+targetIndex)
        if (targetIndex != -1) {
            currentSlideIndex = targetIndex
        }
    }
    // 8. EFECTOS DE LANZAMIENTO (LaunchedEffects)

    LaunchedEffect(connectedUsers) {
        connectedUsersState.value = connectedUsers
    }
    // Inicialización de datos
    LaunchedEffect(courseId) {
        println("url MD:" +contentUrl)
        //viewModel.currentCourseId = courseId
        viewModel.loadCourseData(courseId)
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

    // Sincronización de Idioma
    LaunchedEffect(currentLangCode) {
        viewModel.lang = currentLangCode
        bibleTextsState.value = displayStrings.bible
    }

    LaunchedEffect(contentUrl) {
        if (contentUrl.isNotEmpty()) {
            viewModel.loadMarkdownFromCloudinary( subjectId,contentUrl)
        }
    }

    // Procesamiento de Markdown a Diapositivas
    LaunchedEffect(markdownText) {

        viewModel.updateMarkdownContent(markdownText)
        //if (slides.isNotEmpty()) {
            currentSlideIndex = 0
        //}
        viewModel.broadcastSlideUpdate(0)
        //val slidesIncrementales = parseMarkdownToIncrementalSlides(markdownText)
        //slides.clear()
        //slides.addAll(slidesIncrementales)
        //viewModel.broadcastMarkdownContent()

    }

    // Actualización de estado de la diapositiva actual
    LaunchedEffect(slides, currentSlideIndex) {
        if (slides.isNotEmpty() && currentSlideIndex < slides.size) {
            val slide = slides[currentSlideIndex]
            currentSlideState.value = slide
            currentSlide = slide

            versesInCurrentSlideState.value = extractVerseCitations(slide.contentText)
            totalSlidesState.value = slides.size
            currentSlideIndexState.value = currentSlideIndex
            viewModel.broadcastSlideUpdate(currentSlideIndex)
        } else if (slides.isEmpty()) {
            currentSlideState.value = null
        }
    }

    // Gestión de Foco y Mensajes
    LaunchedEffect(Unit) {
        sidebarActions.registerFocusRecovery {
            recoverFocus()
        }
        recoverFocus()
    }

    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            viewModel.showUserMessage("")
        }
    }

    LaunchedEffect(secondaryDisplayWindows.size, showSecondaryWindowsIntent) {
        if (!showSecondaryWindowsIntent && secondaryDisplayWindows.isEmpty()) {
            mainKeyboardActionAreaRequester.requestFocus()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                // Usamos la lógica de color basada en si es error o no
                // (Nota: Asegúrate de que tu ViewModel tenga la variable 'isError' como propusimos antes)
                Snackbar(
                    containerColor = if (viewModel.isError)
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer,

                    contentColor = if (viewModel.isError)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer,

                    snackbarData = data
                )
            }
        }
    ) { paddingValues ->
        // 2. Colocamos tu ScreenLayout y contenido dentro, respetando el padding del Scaffold
        Box(modifier = Modifier.padding(paddingValues)) {
            ScreenLayout(title = appBarTitle) {
                Surface(
                    color = MaterialTheme.colorScheme.surface, // <--- Color de fondo dinámico
                    modifier = Modifier.fillMaxSize() // <--- Asegura que cubra toda la ventana
                )
                {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 1. ÁREA PRINCIPAL: Maneja el foco y las teclas, y muestra el contenido.
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                //.padding(16.dp)
                                .onFocusChanged { focusState -> // DIAGNÓSTICO DEL FOCO
                                    if (focusState.isFocused) {
                                        println("VENTANA PRINCIPAL: Área de acción TIENE EL FOCO")
                                    } else {
                                        println("VENTANA PRINCIPAL: Área de acción PERDIÓ EL FOCO")
                                    }
                                }
                                .onPreviewKeyEvent { keyEvent: KeyEvent -> // Usamos onPreviewKeyEvent
                                    val handled = handleGlobalKeyEvent(
                                        keyEvent = keyEvent,
                                        onCloseWindows = {
                                            showSecondaryWindowsState.value = false
                                            manageSecondaryDisplays(false, fetchVerseCallback)
                                        }
                                    )

                                    // Lógica específica de la pantalla principal (como el F para abrir ventanas)
                                    if (!handled && keyEvent.type == KeyEventType.KeyDown) {
                                        when (keyEvent.key) {
                                            Key.F -> {
                                                toggleScreens()
                                                return@onPreviewKeyEvent true
                                            }
                                        }
                                    }
                                    handled
                                }
                                .focusRequester(mainKeyboardActionAreaRequester)
                                .focusable(), // Esencial para que onKeyEvent funcione y pueda tener foco
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {



                            // 2. CONTENIDO DE LA VISTA (Ajustado para el overlay de controles)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center // Para centrar la diapositiva en PRESENTATION
                            ) {
                                if (isParsing) {
                                    // 🎯 Muestra cargando SIEMPRE que esté parseando,
                                    // así evitamos que el modo NOTES intente dibujar algo inexistente
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = Color.White)
                                    }
                                } else if (slides.isEmpty()) {
                                    // 🎯 Si terminó de cargar y no hay nada, pantalla de error/vacío
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("No se encontraron diapositivas", color = Color.White)
                                    }
                                } else {
                                    when (viewMode) {
                                        DisplayState.CONTROLS -> {
                                            // --- MODO CONTROLES (Ahora usa flexbox completo) ---
                                            Row(
                                                modifier = Modifier.fillMaxSize().padding(
                                                    bottom = CONTROLS_HEIGHT,
                                                    top = PADDING, // 16.dp
                                                    start = PADDING, // 16.dp
                                                    end = PADDING // 16.dp
                                                ),
                                                horizontalArrangement = Arrangement.spacedBy(HORIZONTAL_SCREEN_PADDING),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // 1. Vista Anterior (Pequeña)
                                                Column(
                                                    modifier = Modifier.fillMaxHeight().weight(1f),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (prevSlide != null) {
                                                            SlideThumbnailView(
                                                                prevSlide.title,
                                                                prevSlide.contentText,
                                                                prevSlide.backgroundColor,
                                                                imageUrl = prevSlide!!.imageUrl,
                                                                svgRawCode = prevSlide.svgRawCode,
                                                                isCurrent = false,
                                                                onVerseHover = { citation ->
                                                                    handleVerseHover(citation)
                                                                },
                                                                fontSize = fontSizeS
                                                            )
                                                        } else {
                                                            SlideThumbnailViewWithoutContent("", Color.Black, false)
                                                        }
                                                    }
                                                    Text(
                                                        text = controlTexts.prevSlide,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                // 2. Vista Actual (Mediana/Grande)
                                                Column(
                                                    modifier = Modifier.fillMaxHeight().weight(2f),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (currentSlide != null) {
                                                            SlideThumbnailView(
                                                                currentSlide!!.title,
                                                                currentSlide!!.contentText,
                                                                currentSlide!!.backgroundColor,
                                                                imageUrl = currentSlide!!.imageUrl,
                                                                svgRawCode = currentSlide!!.svgRawCode,
                                                                isCurrent = true,
                                                                activeCitation = currentCitation,
                                                                onVerseHover = { citation ->
                                                                    handleVerseHover(citation)
                                                                },
                                                                fontSize = fontSizeN
                                                            )
                                                        } else {
                                                            SlideThumbnailViewWithoutContent(
                                                                controlTexts.emptySlide,
                                                                Color.Gray.copy(alpha = 0.8f),
                                                                true
                                                            )
                                                        }
                                                    }
                                                    if (currentSlide != null) {
                                                        //Text(
                                                        //    controlTexts.currentSlideLabel(currentSlideIndex + 1, slides.size),
                                                        //    style = MaterialTheme.typography.bodyLarge,
                                                        //    fontWeight = FontWeight.Bold
                                                        //)
                                                        if (slides.isNotEmpty()) {
                                                            val currentSlide = slides[currentSlideIndex]
                                                            val totalVisible =
                                                                slides.last().masterSlideIndex // El número más alto de masterSlideIndex

                                                            Text(
                                                                text = controlTexts.currentSlideLabel(
                                                                    currentSlide.masterSlideIndex,
                                                                    totalVisible
                                                                ),
                                                                style = MaterialTheme.typography.bodyLarge,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        }
                                                    }
                                                }

                                                // 3. Vista Siguiente (Pequeña)
                                                Column(
                                                    modifier = Modifier.fillMaxHeight().weight(1f),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (nextSlide != null) {
                                                            SlideThumbnailView(
                                                                nextSlide.title,
                                                                nextSlide.contentText,
                                                                nextSlide.backgroundColor,
                                                                imageUrl = nextSlide!!.imageUrl,
                                                                svgRawCode = nextSlide!!.svgRawCode,
                                                                isCurrent = false,
                                                                onVerseHover = { citation ->
                                                                    handleVerseHover(citation)
                                                                },
                                                                fontSize = fontSizeS
                                                            )
                                                        } else {
                                                            SlideThumbnailViewWithoutContent("", Color.Black, false)
                                                        }
                                                    }

                                                    Text(
                                                        text = controlTexts.nextSlide,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        DisplayState.NOTES -> {
                                            // --- MODO NOTAS ---
                                            Row(
                                                modifier = Modifier.fillMaxSize().padding(
                                                    bottom = CONTROLS_HEIGHT,
                                                    top = VERTICAL_SCREEN_PADDING, // 16.dp
                                                    start = HORIZONTAL_SCREEN_PADDING, // 16.dp
                                                    end = HORIZONTAL_SCREEN_PADDING
                                                ),

                                                horizontalArrangement = Arrangement.spacedBy(HORIZONTAL_SCREEN_PADDING)
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxHeight().weight(0.6f),
                                                ) {
                                                    Column(
                                                        modifier = Modifier.fillMaxHeight().weight(0.7f),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Box(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            if (currentSlide != null) {
                                                                SlideThumbnailView(
                                                                    currentSlide!!.title,
                                                                    currentSlide!!.contentText,
                                                                    currentSlide!!.backgroundColor,
                                                                    isCurrent = true,
                                                                    imageUrl = currentSlide!!.imageUrl,
                                                                    svgRawCode = currentSlide!!.svgRawCode,
                                                                    activeCitation = currentCitation,
                                                                    onVerseHover = { citation ->
                                                                        handleVerseHover(citation)
                                                                    },
                                                                    fontSize = fontSizeN
                                                                )
                                                            } else {
                                                                SlideThumbnailViewWithoutContent(
                                                                    controlTexts.emptySlide,
                                                                    Color.Gray.copy(alpha = 0.8f),
                                                                    true
                                                                )
                                                            }
                                                        }
                                                        if (currentSlide != null) {
                                                            val currentSlide = slides[currentSlideIndex]
                                                            val totalVisible =
                                                                slides.last().masterSlideIndex // El número más alto de masterSlideIndex

                                                            Text(
                                                                text = controlTexts.currentSlideLabel(
                                                                    currentSlide.masterSlideIndex,
                                                                    totalVisible
                                                                ),
                                                                style = MaterialTheme.typography.bodyLarge,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                            /*Text(
                                                            controlTexts.currentSlideLabel(currentSlideIndex + 1, slides.size),
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            fontWeight = FontWeight.Bold
                                                        )*/
                                                        }
                                                    }
                                                    Column(
                                                        modifier = Modifier.fillMaxHeight().weight(0.3f),

                                                        ) {
                                                        Box(
                                                            modifier = Modifier.fillMaxWidth(0.5f).weight(1f),
                                                            //contentAlignment = Alignment.TopStart
                                                        ) {
                                                            if (nextSlide != null) {
                                                                SlideThumbnailView(
                                                                    nextSlide.title,
                                                                    nextSlide.contentText,
                                                                    nextSlide.backgroundColor,
                                                                    imageUrl = nextSlide!!.imageUrl,
                                                                    svgRawCode = nextSlide!!.svgRawCode,
                                                                    isNotesMode = true,
                                                                    isCurrent = false,
                                                                    onVerseHover = { citation ->
                                                                        handleVerseHover(citation)
                                                                    },
                                                                    fontSize = fontSizeS
                                                                )
                                                            } else {
                                                                SlideThumbnailViewWithoutContent("", Color.Black, true)
                                                            }

                                                        }
                                                        Text(
                                                            text = controlTexts.nextSlide,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontWeight = FontWeight.Bold, // 🔑 Negrita
                                                            modifier = Modifier.fillMaxWidth(0.5f),
                                                            textAlign = TextAlign.Center
                                                        )
                                                    }
                                                }

                                                // Notas (70% del ancho)
                                                Column(
                                                    modifier = Modifier
                                                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 16.dp)
                                                        .weight(0.4f).fillMaxHeight().background(Color(0xFFF0F0F0))
                                                        .padding(16.dp)
                                                ) {
                                                    Text(
                                                        controlTexts.presenterNotes,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(Modifier.height(8.dp))
                                                    Text(
                                                        text = currentSlide?.contentText ?: "No hay notas disponibles.",
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                }

                                            }
                                        }

                                        DisplayState.PRESENTATION -> {
                                            // --- MODO PRESENTATION (FULLSCREEN) ---
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (currentSlide != null) {
                                                    SlideThumbnailView(
                                                        currentSlide!!.title,
                                                        currentSlide!!.contentText,
                                                        currentSlide!!.backgroundColor,
                                                        imageUrl = currentSlide!!.imageUrl,
                                                        svgRawCode = currentSlide!!.svgRawCode,
                                                        isCurrent = true,
                                                        activeCitation = currentCitation,
                                                        onVerseHover = { citation ->
                                                            handleVerseHover(citation)
                                                        },
                                                        fontSize = fontSizeB, // <-- USANDO FUENTE GRANDE
                                                        fullScreen = true
                                                    )
                                                } else {
                                                    Text(
                                                        controlTexts.emptySlide,
                                                        style = MaterialTheme.typography.headlineMedium
                                                    )
                                                }
                                            }
                                        }
                                    } // FIN DEL SWITCH DE VISTA
                                }
                            }
                            // Este Spacer ya no es necesario aquí. Los controles se mueven al Box raíz.
                            // Spacer(modifier = Modifier.height(16.dp))
                        }
                        // --- FIN DEL ÁREA PRINCIPAL ---




                        //val isShowingCitation = currentCitation != null
                        //val isShowingBible by remember { derivedStateOf { showBibleSidebarState.value } }

        // --- Estructura Principal ---
                        if (showExamDialog) {
                            LaunchedEffect(Unit) {
                                viewModel.loadQuizzesForSubject(subjectId, courseId)
                            }

                            ExamSelectorDialog(
                                courseId = courseId,
                                onDismiss = { showExamDialog = false },
                                //onExamSelected = { examId ->
                                    // viewModel.startExam(examId)
                                //    showExamDialog = false
                                //},
                                texts = controlTexts,
                                viewModel = viewModel,
                            )
                        }
                        DisplayControls(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            visible = controlsVisible,
                            subjectName = subjectName,
                            currentSlideIndex = currentSlideIndex,
                            totalSlides = slides.size,
                            viewMode = viewMode,
                            isShowingScreens = showSecondaryWindowsIntent,
                            selectedBibleId = selectedBibleId,
                            availableBibles = availableBibles,
                            showBibleSidebar = showBibleSidebar,
                            viewModel = viewModel,
                            onBack = onBack,
                            onNavigateSlide = { newIndex ->
                                currentSlideIndex = newIndex
                                //jumpToMasterSlide(newIndex)
                                //scope.launch { viewModel.broadcastSlideUpdate(newIndex) }
                            },
                            onChangeViewMode = {
                                viewMode = when (viewMode) {
                                    DisplayState.CONTROLS -> DisplayState.NOTES
                                    DisplayState.NOTES -> DisplayState.PRESENTATION
                                    DisplayState.PRESENTATION -> DisplayState.CONTROLS
                                }
                                mainKeyboardActionAreaRequester.requestFocus()
                            },
                            onToggleScreens = {
                                toggleScreens()

                                //if (!showSecondaryWindowsIntent) {
                                //    showSecondaryWindowsIntent = true
                                //    manageSecondaryDisplays(true, fetchVerseCallback)
                                //}
                            },
                            onSelectBible = { id ->
                                selectedBibleId = id
                                currentBibleIdState.value = id
                            },
                            onToggleBibleSidebar = {
                                showBibleSidebarState.value = !showBibleSidebarState.value
                            },
                            onChangeFontSize = { step ->
                                val newValue = currentBibleFontSize + step
                                if (newValue in minFontSize..maxFontSize) {
                                    currentBibleFontSizeState.value = newValue
                                }
                            },
                            onMarkdownLoaded = { newContent ->
                                //localMarkdownContent = newContent
                                viewModel.updateMarkdownContent(newContent)
                                viewModel.openMarkdownEditor()
                                //slides = parseMarkdownToIncrementalSlides(newContent)
                                //currentSlideIndex = 0
                            },
                            focusRequester = mainKeyboardActionAreaRequester,
                            texts = controlTexts,
                            onOpenExam = { showExamDialog = true },
                            visualCurrentIndex= currentVisualIndex,
                            visualTotalCount= totalVisualCount,
                            jumpToMasterSlide = jumpToMasterSlide
                        )

                        // 4. Mensaje (Overlay superior)
                        Text(
                            text = message ,
                            //color = Color.Green, // Un color que destaque para diagnóstico
                            style = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .align(Alignment.TopEnd) // Alinea a la esquina superior derecha
                                .padding(PADDING) // Añade un poco de margen
                                .background(Color.Green.copy(alpha = 0.3f)) // Fondo ligero para visibilidad
                                .padding(4.dp)
                        )
                        PrimaryButton(
                            onClick = {
                                controlsVisible = !controlsVisible
                                mainKeyboardActionAreaRequester.requestFocus()
                            },
                            modifier = Modifier.align(Alignment.BottomEnd).padding(PADDING),
                            icon = {
                                Icon(
                                    imageVector = if (controlsVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (controlsVisible) "Ocultar controles" else "Mostrar controles"
                                )}
                        )
                        // ⭐️ NUEVO BOTÓN: Cargar Markdown ⭐️


                        if (showBibleSidebar) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.3f)
                                    .fillMaxHeight()
                                    .padding(bottom = 3.dp, top = 3.dp, end = 3.dp)
                                    .align(Alignment.CenterEnd)
                                    // 🎯 CLAVE: Si el mouse entra aquí, cancelamos el cierre programado
                                    .onPointerEvent(PointerEventType.Enter) {
                                        bibleCloseJob?.cancel()
                                    }
                                    // Si el mouse sale de la biblia, que se cierre normalmente
                                    .onPointerEvent(PointerEventType.Exit) {
                                        handleVerseHover(null)
                                    }
                            ) {
                                VerseDisplayArea(
                                    bibleId = selectedBibleId,
                                    citation = if(currentCitation.isNullOrEmpty()) null else currentCitation,
                                    onFetchVerseContent = fetchVerseCallback,
                                    modifier = Modifier.fillMaxSize(), // Ahora el Box padre controla el tamaño
                                    currentBibleFontSize = currentBibleFontSize,
                                    isMaster = true,
                                    texts = bibleTexts
                                )
                            }
                        }
                        if (showCustomWindow) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterEnd // Misma posición que la biblia
                            ) {
                                CustomEmptyOverlay(users = connectedUsers)
                            }
                        }
                        LoadingOverlay(isLoading = isLoading)

                    }
                }
            }
        }
    }
}

