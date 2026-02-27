package com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.jaco.cc3d.core.config.CloudinaryConfig
import com.jaco.cc3d.core.config.CloudinaryConfig.getMdUrl
import com.jaco.cc3d.core.util.LoadingHandler
import com.jaco.cc3d.data.local.cache.FileCacheManager
import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.domain.models.QuizQuestion
import com.jaco.cc3d.domain.models.ScheduledQuiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject // Usaremos @Inject para la inyección (si no usas Hilt)
import kotlinx.coroutines.launch
import com.jaco.cc3d.domain.usecases.bible.GetVerseUseCase
import com.jaco.cc3d.domain.usecases.quizAttempt.CreateAttempt
import com.jaco.cc3d.domain.usecases.quizAttempt.SubmitQuiz
import com.jaco.cc3d.domain.usecases.quizQuestion.GetAllQuizQuestions
import com.jaco.cc3d.domain.usecases.scheduleQuiz.GetScheduledQuizzesByCourseUseCase
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.util.StudentDisplayResources
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models.SlideContent
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.parseMarkdownToIncrementalSlides
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import java.net.URL

private const val INITIAL_RETRY_DELAY_MS = 3000L // 3 segundos de espera inicial
const val MAX_RETRY_COUNT = 5 // Número máximo de reintentos antes de parar
private const val MAX_RETRY_DELAY_MS = 60000L
// 🔑 CLASE DEL VIEWMODEL (ScreenModel en Voyager)
sealed class ConnectionState {
    object Disconnected : ConnectionState()
    data class Searching(val host: String, val port: Int) : ConnectionState()
    data class Connected(val subjectName: String) : ConnectionState()
    object Error : ConnectionState()
    object NotFound : ConnectionState()
}

class StudentDisplayViewModel @Inject constructor(
    //private val authRepository: AuthRepository // Inyectamos el Repositorio (Dagger)
    private val getVerseUseCase: GetVerseUseCase,
    private val getScheduledQuizzesUseCase: GetScheduledQuizzesByCourseUseCase,
    private val getAllQuizQuestionsUseCase: GetAllQuizQuestions,
    private val createAttemptUseCase: CreateAttempt,
    private val submitQuizUseCase: SubmitQuiz,
    private val tokenManager: EncryptedDesktopTokenManager,

) : ScreenModel , LoadingHandler  {

    // Definimos constantes para el reintento
    var lang by mutableStateOf("es")
    private val strings get() = StudentDisplayResources.get(lang)
    val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _message: MutableStateFlow<String?> = MutableStateFlow(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading = _isLoading.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    override val errorEvents = _errorEvents.asSharedFlow()

    // 1. Estados expuestos a la UI (ReadOnlyStateFlow)

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    var currentAttemptId by mutableStateOf<String?>(null)
        private set
    var isSubmittingQuiz by mutableStateOf(false)
        private set
    // Asegúrate de inyectar el nuevo Use Case

    private val _currentSlideNumber = MutableStateFlow(0)
    val currentSlideNumber: StateFlow<Int> = _currentSlideNumber

    private val _slides = MutableStateFlow<List<SlideContent>>(emptyList())
    val slides: StateFlow<List<SlideContent>> = _slides.asStateFlow()

    // Estado interno para rastrear la IP y controlar la reconexión.
    private val _currentServerIp = MutableStateFlow("192.168.1.1")
    val currentServerIp: StateFlow<String> = _currentServerIp
    private val _currentRetryCount = MutableStateFlow(0)
    val currentRetryCount: StateFlow<Int> = _currentRetryCount

    private val _retryDelayMessage = MutableStateFlow<String?>(null)
    val retryDelayMessage: StateFlow<String?> = _retryDelayMessage

    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()
    var isLoadingQuestions by mutableStateOf(false)

    // Coroutine Job para manejar el ciclo de vida de la conexión WebSocket.
    private var connectionJob: Job? = null
    //private val _currentPort = MutableStateFlow(8080)
    private val _currentCourseId = MutableStateFlow<String?>(null)
    private val _markdownContent = MutableStateFlow("""
        # Hola Ruben
        ## Como estas Hechos 28:17-31, 1 Corintios 9:15-18
        ## Espero que bien Lucas 2:6b Juan 3:4c   
        
        # Hola Juan
        ## Como Lucas 1:26-2:7 vas
        ## Espero Romanos 10:17 que vayas bien.    
    """.trimIndent())
    val markdownContent: StateFlow<String> = _markdownContent

    private val _isManualConnectionEnabled = MutableStateFlow(false)
    val isManualConnectionEnabled = _isManualConnectionEnabled.asStateFlow()

    private val _currentSubjectName = MutableStateFlow("")

    private val _availableQuizzes = MutableStateFlow<List<ScheduledQuiz>>(emptyList())
    val availableQuizzes = _availableQuizzes.asStateFlow()

    // Estado simple para saber si hay quizzes activos (para el punto rojo)
    private val _hasPendingQuizzes = MutableStateFlow(false)
    val hasPendingQuizzes = _hasPendingQuizzes.asStateFlow()

    private var activeSession: DefaultClientWebSocketSession? = null

    init {
        _connectionState.value = ConnectionState.Disconnected
        screenModelScope.launch {
            _markdownContent.collect { md ->
                val slidesIncrementales = parseMarkdownToIncrementalSlides(md)
                _slides.value = slidesIncrementales
            }
        }
    }

    fun loadMarkdownContent(content: String) {
        viewModelScope.launch {
            // 🎯 Usar la misma función que el maestro para generar la lista acumulada
            val incrementalSlides = parseMarkdownToIncrementalSlides(content)
            _slides.value = incrementalSlides
        }
    }

    fun initialize(courseId: String, subjectName: String) {
        _connectionState.value = ConnectionState.Disconnected
        _currentCourseId.value = courseId
        _currentSubjectName.value = subjectName

        //loadScheduledQuizzes(courseId)
        // Solo iniciamos la conexión si el switch manual está ON
        if (_isManualConnectionEnabled.value) {
            startConnectionLoop()

        }
    }

    fun loadMarkdownFromCloudinary(subjectId: String, contentId: String) {
        screenModelScope.launch {
            val finalContent = runWithLoading(_isLoading, _errorEvents) {
                val cachedText = FileCacheManager.getCachedFile(subjectId, contentId)
                if (cachedText != null) {
                    println("Caché: Cargando versión actual")
                    cachedText
                } else {
                    val fullUrl = getMdUrl(contentId)
                    val downloadedText = withContext(Dispatchers.IO) {
                        URL(fullUrl).readText()
                    }
                    FileCacheManager.saveToCache(subjectId, contentId, downloadedText)
                    downloadedText
                }
            }
            finalContent?.let {
                _markdownContent.value = it
                _slides.value = parseMarkdownToIncrementalSlides(it)
            }
        }
    }

    fun goToSlide(index: Int) {
        if (index in 0 until _slides.value.size) {
            _currentSlideNumber.value = index
        }
    }

    fun jumpToMasterSlide(masterPage: Int) {
        val targetIndex = _slides.value.indexOfFirst { it.masterSlideIndex == masterPage }
        if (targetIndex != -1) {
            _currentSlideNumber.value = targetIndex
        }
    }

    fun loadScheduledQuizzes(courseId: String) {
        screenModelScope.launch {
            getScheduledQuizzesUseCase(courseId = courseId, status = 1) // status 1 = Activo
                .onSuccess { list ->
                    _availableQuizzes.value = list
                    _hasPendingQuizzes.value = list.isNotEmpty()
                }
                .onFailure {
                    _availableQuizzes.value = emptyList()
                    _hasPendingQuizzes.value = false
                }
        }
    }

    fun startQuiz(scheduledQuizId: String) {
        screenModelScope.launch {
            isLoadingQuestions = true

            createAttemptUseCase(
                scheduledQuizId = scheduledQuizId,
               // studentId = studentId, // ID del alumno logueado
                amount = 10,
                isRandom = true
            ).onSuccess { result ->
                // Guardamos el ID del intento para el "Submit" posterior
                currentAttemptId = result.attempt.id

                // Cargamos las preguntas en la lista que usa el Dialog
                _quizQuestions.value = result.questions

                isLoadingQuestions = false
            }.onFailure { error ->
                println("Error al iniciar el quiz: ${error.message}")
                _quizQuestions.value = emptyList()
                currentAttemptId = null
                isLoadingQuestions = false
            }
        }
    }

    fun submitQuiz(answers: Map<String, Int>) {
        val attemptId = currentAttemptId ?: return

        screenModelScope.launch {
            isSubmittingQuiz = true
            submitQuizUseCase(
                attemptId = attemptId,
                answers = answers
            ).onSuccess { gradedAttempt ->
                // Aquí podrías mostrar una notificación de éxito o la nota obtenida
                sendMessageToTeacher("STUDENT_FINISH_QUIZ:$attemptId")

                println("Quiz finalizado con nota: ${gradedAttempt.totalScoreObtained}")
                isSubmittingQuiz = false
                currentAttemptId = null // Limpiamos para el siguiente
            }.onFailure {
                isSubmittingQuiz = false
                // Manejar error de red al enviar
            }
        }
    }

    fun loadQuizQuestions(quizTemplateId: String) {
        screenModelScope.launch {
            isLoadingQuestions = true
            getAllQuizQuestionsUseCase(
                quizTemplateId = quizTemplateId,
                limit = 10,   // Cantidad de preguntas
                isRandom = true
            ).onSuccess { response ->
                _quizQuestions.value = response.docs
                isLoadingQuestions = false
            }.onFailure {
                _quizQuestions.value = emptyList()
                isLoadingQuestions = false
                // Aquí podrías manejar un error de red
            }
        }
    }

    fun clearQuestions(){
        _quizQuestions.value = emptyList()
    }

    fun toggleConnection() {
        if (_isManualConnectionEnabled.value) {
            _isManualConnectionEnabled.value = false
            stopConnection()
        } else {
            val courseId = _currentCourseId.value
            val ip = _currentServerIp.value
            if (!courseId.isNullOrEmpty() && ip.isNotEmpty()) {
                _isManualConnectionEnabled.value = true
                startConnectionLoop()
            } else {
                //_connectionState.value = "Falta IP o ID de Curso"
                _connectionState.value = ConnectionState.Error// strings.connection.statusError
            }
        }
    }

    private fun stopConnection() {
        connectionJob?.cancel()
        //_connectionState.value = "Desconectado"
        _connectionState.value = ConnectionState.Disconnected// strings.connection.statusDisconnected
        _currentRetryCount.value = 0
        _retryDelayMessage.value = null
    }

    // 3. El núcleo de la conexión con escaneo de puertos y reintentos
    private fun startConnectionLoop() {
        val courseId = _currentCourseId.value ?: return
        val host = _currentServerIp.value

        connectionJob?.cancel()
        connectionJob = screenModelScope.launch {
            var retryCount = 0

            while (isActive && _isManualConnectionEnabled.value) {
                _retryDelayMessage.value = null
                var foundTeacher = false
                _currentRetryCount.value = retryCount + 1

                // ESCANEO DE PUERTOS (8080 - 8089)
                for (portToTry in 8080..8089) {
                    if (!isActive || !_isManualConnectionEnabled.value) break

                    _connectionState.value = ConnectionState.Searching(host, portToTry)

                    try {
                        // Llamamos a la función que maneja el WebSocket real
                        connectAndReceiveSlides(host, portToTry, courseId).collect { slideIndex ->
                            foundTeacher = true
                            retryCount = 0
                            _currentRetryCount.value = 0
                            _retryDelayMessage.value = null
                            //_currentSlideNumber.value = slideIndex
                            goToSlide(slideIndex)
                            //_connectionState.value = "Sincronizado"
                            _connectionState.value = ConnectionState.Connected(_currentSubjectName.value)
                        }
                    } catch (e: Exception) {
                        if (e.message?.contains("COURSE_NOT_FOUND") == true || e.message?.contains("4003") == true) {
                            _connectionState.value = ConnectionState.Error //strings.connection.statusError
                            _isManualConnectionEnabled.value = false // Apagamos el toggle
                            return@launch
                        }
                        // Si falla este puerto, el loop de portToTry sigue al siguiente
                    }
                }

                // SI FALLÓ EL ESCANEO COMPLETO
                if (!foundTeacher && _isManualConnectionEnabled.value) {
                    retryCount++
                    if (retryCount >= MAX_RETRY_COUNT) {
                        _connectionState.value = ConnectionState.NotFound
                        delay(MAX_RETRY_DELAY_MS)
                        retryCount = 0
                    } else {
                        // Espera con cuenta regresiva
                        val delayTime = (INITIAL_RETRY_DELAY_MS * Math.pow(2.0, (retryCount - 1).toDouble())).toLong()
                        for (i in (delayTime / 1000) downTo 1) {
                            if (!_isManualConnectionEnabled.value) break
                            _retryDelayMessage.value = strings.connection.retryIn(i.toInt())
                            delay(1000)
                        }
                    }
                }
            }
        }
    }

    fun nextSlide() {
        val total = _slides.value.size
        val current = _currentSlideNumber.value ?: 0
        if (current < total - 1) { // Límite: último índice es size - 1
            _currentSlideNumber.value = current + 1
        }
    }

    fun previousSlide() {
        val current = _currentSlideNumber.value ?: 0
        if (current > 0) {
            _currentSlideNumber.value = current - 1
        }
    }

    // Opcional: Para que el usuario pueda volver a la posición 1 rápidamente
    fun resetToFirstSlide() {
        _currentSlideNumber.value = 1
    }

    suspend fun fetchVerseContent(bibleId: String, citation: String): String {

        return try {
            val verses = getVerseUseCase(bibleId, citation)

            if (verses.isEmpty()) {
                //return "Referencia no encontrada."
                return strings.bible.noVerse
            }

            // 2. Extracción de la referencia principal (asumiendo que todos los versículos son del mismo libro/capítulo)
            val firstVerse = verses.first()
            val fullCitation = "${citation}" // Ej: "Juan 3"

            // 3. Formatea la lista de versículos
            // Se añade un encabezado con la cita y luego la lista de versículos
            val versesText = verses.joinToString(separator = "\n") {
                // Se corrige el campo a 'it.scripture'
                //"${it.chapter}:${it.verse} ${it.text}"
                //"{\\cf7\\b ${it.chapter}:${it.verse}\\b0} ${it.text}"
                //"{\\cf7 ${it.chapter}:${it.verse}} ${it.text}"
                "{\\cf7\\b\\i ${it.chapter}:${it.verse}\\i0\\b0} ${it.text}"
            }

            // 4. Se devuelve el resultado completo y bien formateado
            //"\n{\\b $citation\\par}\n\n$versesText"
            versesText

        } catch (e: Exception) {
            // Loggear el error real para depuración
            println("ERROR al obtener versículo para '$citation': ${e.message}")
            // Mensaje de error amigable para el usuario
            "Error interno al cargar la Biblia: ${e.message}"
        }
    }

    fun setServerIp(newIp: String) {
        //if (newIp != _currentServerIp.value) {
        _currentServerIp.value = newIp
        if (_isManualConnectionEnabled.value) startConnectionLoop()
            // La actualización de _currentServerIp dispara el LaunchedEffect implícito
            // del init block, cancelando la conexión anterior e iniciando la nueva.
        //}
    }

    fun connectAndReceiveSlides(
        host: String,
        port: Int,
        courseId: String
    ): Flow<Int> = flow {
        val client = HttpClient(CIO) { install(WebSockets) }

        try {
            val userData = tokenManager.getUserData()
            val userId = userData?._id ?: "unknown"
            val pathWithParams = "/display/$courseId?" +
                    "userId=$userId&"
            client.webSocket(
                method = HttpMethod.Get,
                host = host,
                port = port,
                path = pathWithParams
            ) {
                // Si llegamos aquí, el servidor SÍ aceptó el curso
                activeSession = this
                _currentRetryCount.value = 0
                //_connectionState.value = "Sincronizado con: $courseId"
                _connectionState.value = ConnectionState.Connected(_currentSubjectName.value)
                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val message = frame.readText()
                            if (message.startsWith("SLIDE_UPDATE:")) {
                                val num = message.substringAfter(":").toIntOrNull()
                                if (num != null) emit(num)
                            }
                        }
                    }
                } finally {
                    // 🎯 LIMPIAMOS LA SESIÓN: Al cerrarse el socket por cualquier motivo
                    activeSession = null
                }
            }
        } catch (e: Exception) {
            // 🚨 CAPTURA DEL ERROR DE VALIDACIÓN
            val errorMessage = e.localizedMessage ?: ""

            if (errorMessage.contains("COURSE_NOT_FOUND") || errorMessage.contains("4003")) {
                //_connectionState.value = "Error: El curso '$courseId' no existe en este servidor."
                _connectionState.value = ConnectionState.Error
                // Cancelamos el job para que deje de reintentar
                connectionJob?.cancel()
            } else {
                throw e // Si es error de red (IP mala), dejamos que tu lógica de reintentos funcione
            }
        } finally {
            client.close()
        }
    }

    fun sendMessageToTeacher(message: String) {
        screenModelScope.launch {
            try {
                activeSession?.send(Frame.Text(message))
                println("📤 Enviado al profesor: $message")
            } catch (e: Exception) {
                println("❌ Error al enviar mensaje: ${e.message}")
            }
        }
    }

    fun onCleared() {
        viewModelScope.cancel()
    }
}
