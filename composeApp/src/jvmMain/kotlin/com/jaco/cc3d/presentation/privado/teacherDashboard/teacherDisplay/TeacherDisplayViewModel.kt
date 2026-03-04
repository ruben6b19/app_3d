package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.jaco.cc3d.core.config.CloudinaryConfig.getMdUrl
import com.jaco.cc3d.core.util.LoadingHandler
import com.jaco.cc3d.data.network.Connection
import com.jaco.cc3d.data.local.cache.FileCacheManager
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.domain.models.QuizTemplate
import com.jaco.cc3d.domain.models.ScheduledQuiz
import com.jaco.cc3d.domain.models.ScheduledQuizDomainRequest
import com.jaco.cc3d.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject // Usaremos @Inject para la inyección (si no usas Hilt)
import kotlinx.coroutines.launch

import com.jaco.cc3d.domain.usecases.bible.GetVerseUseCase
import com.jaco.cc3d.domain.usecases.course.GetCourseById
import com.jaco.cc3d.domain.usecases.quizTemplate.GetAllQuizTemplates
import com.jaco.cc3d.domain.usecases.scheduleQuiz.ScheduleQuiz
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.currentScheduledQuizState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models.SlideContent
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.DisplayResources
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.parseMarkdownToIncrementalSlides
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.coroutineScope
import java.util.Collections
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.server.application.* // Para Application
import io.ktor.server.plugins.origin
import java.time.Duration
import kotlin.concurrent.thread

import io.ktor.server.routing.*
import java.net.BindException
import java.net.Inet4Address
import java.net.NetworkInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.net.URL

// 🔑 CLASE DEL VIEWMODEL (ScreenModel en Voyager)
class TeacherDisplayViewModel @Inject constructor(
    //private val authRepository: AuthRepository // Inyectamos el Repositorio (Dagger)
    private val getVerseUseCase: GetVerseUseCase,
    private val getAllQuizTemplates: GetAllQuizTemplates,
    private val scheduleQuiz: ScheduleQuiz,
    private val getCourseById: GetCourseById
) : ScreenModel, LoadingHandler {

    // 🔑 Estado mutable y visible para la UI
    //private val _state = MutableStateFlow(LoginState())
    //val state: StateFlow<LoginState> = _state
    var lang by mutableStateOf("es")
    private var websocketServer: NettyApplicationEngine? = null
    private var websocketThread: Thread? = null

   // val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())

    private val _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled: StateFlow<Boolean> = _buttonEnabled
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    var isError by mutableStateOf(false)
        private set

    var isSchedulingQuiz by mutableStateOf(false)
    private set

    private val _isServerRunning = MutableStateFlow(false)
    val isServerRunning: StateFlow<Boolean> = _isServerRunning

    //private val _isLoading = MutableStateFlow(false)
    //val isLoading: StateFlow<Boolean> = _isLoading
    private val _isLoading = MutableStateFlow(false)
    override val isLoading = _isLoading.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    override val errorEvents = _errorEvents.asSharedFlow()

    private val _isParsing = MutableStateFlow(false)
    val isParsing: StateFlow<Boolean> = _isParsing

    private val _slides = MutableStateFlow<List<SlideContent>>(emptyList())
    val slides: StateFlow<List<SlideContent>> = _slides

    private val _currentScheduledQuiz = MutableStateFlow<ScheduledQuiz?>(null)
    val currentScheduledQuiz: StateFlow<ScheduledQuiz?> = _currentScheduledQuiz

    // 🔑 Estado para controlar la visibilidad del editor/visor de Markdown
    private val _isMarkdownEditorOpen = MutableStateFlow(false)
    val isMarkdownEditorOpen: StateFlow<Boolean> = _isMarkdownEditorOpen

    // 1. Cambiamos la lista simple por un Mapa: ID_CURSO -> CONJUNTO DE CONEXIONES
    private val courseConnections = Collections.synchronizedMap(mutableMapOf<String, MutableSet<Connection>>())

    // 2. Variable para guardar el ID del curso actual (necesario para el broadcast)
    var currentCourseId: String = ""
    private var currentPort = 8080

    val connectedUsers: List<String>
        get() = courseConnections[currentCourseId]?.map { it.name } ?: emptyList()

    val connectedCount: Int
        get() = courseConnections[currentCourseId]?.size ?: 0

    private val _currentUsers = mutableStateOf<List<Connection>>(emptyList())
    val currentUsers: androidx.compose.runtime.State<List<Connection>> = _currentUsers

    // Llama a esta función cada vez que alguien se conecte o desconecte
    //private fun updateParticipants() {
    //    _currentUsers.value = courseConnections[currentCourseId]?.toList() ?: emptyList()
    //}
    private fun updateParticipants() {
        // Usamos el ID del curso actual para sacar la lista del HashMap
        //val connections = courseConnections[currentCourseId]?.toList() ?: emptyList()
        //_currentUsers.value = connections
        //println("Lista de participantes actualizada: ${connections.size} conectados")

        val room = courseConnections[currentCourseId]

        // 🎯 IMPORTANTE: Las actualizaciones de estado de UI deben ser atómicas
        val newParticipants = room?.toList() ?: emptyList()

        // Si estás dentro de un scope de corrutina de red, asegúrate de volver al Main
        _currentUsers.value = newParticipants
    }

    // 🔑 Estado que contendrá el texto a editar o mostrar
    private val _markdownContent = MutableStateFlow("""
        ### Romanos 1:1
        aqui viene el versículo
        
        | Nombre | Edad |
        | ------ | ---- |
        | Jorge| 25   |
        | Jose| 25   |
        
        # Hola Ruben 2
        ## Como 1 cor 15:1-4 estas Hechos 28:17-31, 1 Corintios 9:15-18
        Espero que bien Lucas 2:6b Juan 3:4c   
        
        ### Romanos 1:1
        aqui viene el versículo

        #### Pablo
        pablo se presenta como Pablo, no saulo.
        
        #### Incluye:
        ##### Tus pecados presentes y futuros
        ##### Tus futuras dudas
        ##### Tus futuras caídas espirituales
        ##### Ni siquiera esos escenarios pueden romper el amor de Dios hacia ti.
        
        https://cdn.pixabay.com/photo/2017/01/13/01/22/rocket-1976107_1280.png
        
        https://images.pexels.com/photos/15286/pexels-photo.jpg
        
        # Hola Juan
        ## Como Lucas 1:26-2:7 vas
        Espero Romanos 10:17 que vayas bien. 
        
        ## Hola Pepe
        
        ## Hola jorge
        ## Espero Romanos 10:17 que vayas bien.  
    """.trimIndent())

    private val _markdownContent2 = MutableStateFlow("""
        # 🙏 Pilares Génesis 1:4a Lucas 2:6b Juan 3:4c de la Vida Cristiana: Fe, Lucas Esperanza y Amor

        ### Romanos 2:3a "¿Y piensas esto, oh hombre…" 
        #### Pablo introduce una pregunta retórica que apela al razonamiento del moralista.
       
        # Encabezado 1 (Título principal / H1)
        ## Encabezado 2 (Sección principal / H2)
        ### Encabezado 3 (Subsección / H3)
        #### Encabezado 4 (Sub-subsección / H4)
        
        # Hola Ruben
        ## Como estas
        ## Espero que bien.

        casos especiales
        Hechos 28:17-31, 1 Corintios 9:15-18 caso simple judas 3 otro caso simple  Romanos 9, 10, 11 
        
        ## 📜 La Importancia 1 Juan de la Fe

        La **Fe** es el cimiento sobre el cual se construye la relación con Dios Salmos 103:1, 3, 5. Mateo 7:7, 11 No es 1 Juan 4:8 solo una creencia pasiva, sino una acción que nos mueve y nos da certeza, incluso cuando la evidencia física no está presente. Como nos recuerda la Escritura:

        * Hebreos 11:1
        * Romanos 10:17
        * Santiago 2:17

        ## ❤️ El Mandato del Amor Lucas 1:26-2:7

        El **Amor** es el vínculo perfecto y el  Colosenses 3:14 mayor de los *mandamientos*. Es la esencia de Dios y el distintivo de un verdadero creyente. Todo lo que hacemos debe estar motivado por esta virtud.

        prueba 
        > El amor es fundamental en todas nuestras interacciones y decisiones diarias. Es la prueba tangible de nuestra fe.

        Citas clave sobre el amor:

        * 1 Corintios 13:4-5
        * Juan 3:16
        * Colosenses 3:14
        * 1 Juan 4:8

        ## 🌅 Ancla de la Esperanza

        La **Esperanza** es la *expectativa firme* de lo que ha sido prometido. Nos da consuelo en medio de las pruebas y nos impulsa hacia adelante, sabiendo que hay un futuro y una recompensa seguros.

        La Biblia nos anima a mantener esta esperanza:

        1.  texto Jeremías 29:11 
        2.  texto Romanos 15:13
        3.  texto Hebreos 6:19

        ### Conclusión

        Al final, estas tres virtudes están *interconectadas*. El apóstol Pablo lo resume perfectamente:

        * 1 Corintios 13:13
    """.trimIndent())
    val markdownContent: StateFlow<String> = _markdownContent

    // Función para abrir el editor
    fun openMarkdownEditor(newContent: String? = null) {
        if (newContent != null) {
            _markdownContent.value = newContent
        }
        _isMarkdownEditorOpen.value = true
    }

    // Función para cerrar el editor
    fun closeMarkdownEditor() {
        _isMarkdownEditorOpen.value = false
    }

    // Función para actualizar el contenido del editor (si permites edición)
    fun updateMarkdownContent(newContent: String) {
        screenModelScope.launch {
            _isParsing.value = true
            //_currentSlideIndex.value = 0
            _slides.value = emptyList()
            try {
                // Llamamos a la utilidad suspendida
                val result = parseMarkdownToIncrementalSlides(newContent)
                _slides.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isParsing.value = false
            }
        }
    }

    private var currentCourseStudents: List<User> = emptyList()

    // Función que deberías llamar cuando el profesor entra a este curso
    fun setCurrentCourse(course: Course) {
        this.currentCourseId = course.id
        this.currentCourseStudents = course.students // Aquí ya vienen del backend/Room
    }

    fun loadCourseData(courseId: String) {
        screenModelScope.launch {
            // Buscamos el curso en el repositorio (que debería consultar Room)
            getCourseById(courseId).onSuccess { course ->
                setCurrentCourse(course)
                println("Datos del curso cargados: ${course.students.size} alumnos listos.")
            }.onFailure {
                println("Error al cargar datos locales del curso: ${it.message}")
            }
        }
    }

    fun loadMarkdownFromCloudinary(subjectId: String, contentId: String) {
        screenModelScope.launch {
            val finalContent = runWithLoading(_isLoading, _errorEvents) {
                // 1. Buscamos en caché usando ambos IDs
                // Si el contentId cambió en el servidor, esto devolverá null porque el nombre no coincide exactamente
                val cachedText = FileCacheManager.getCachedFile(subjectId, contentId)

                if (cachedText != null) {
                    println("Caché: Cargando versión actual")
                    cachedText
                } else {
                    // 2. Si no coincide el UID, descargamos el nuevo
                    val fullUrl = getMdUrl(contentId)
                    val downloadedText = withContext(Dispatchers.IO) {
                        URL(fullUrl).readText()
                    }
                    // 3. Guardamos. El manager se encarga de borrar el UID viejo (el huérfano)
                    FileCacheManager.saveToCache(subjectId, contentId, downloadedText)

                    downloadedText
                }
            }
            finalContent?.let {
                _markdownContent.value = it
                _slides.value = parseMarkdownToIncrementalSlides(it)
                println("hola")
            }
        }
    }

    suspend fun broadcastMarkdownContent() {
        if (!isServerRunning.value || currentCourseId.isBlank()) return

        // Preparamos el mensaje
        val messageToBroadcast = "MARKDOWN_CONTENT:${_markdownContent.value}"

        // Obtenemos las conexiones de la sala actual
        val roomConnections = courseConnections[currentCourseId]?.toList() ?: return

        coroutineScope {
            roomConnections.forEach { connection ->
                launch {
                    try {
                        connection.session.send(Frame.Text(messageToBroadcast))
                    } catch (e: Exception) {
                        println("Error enviando Markdown a ${connection.name}")
                    }
                }
            }
        }
    }
    //val bibleDao = SqliteBibleDao("ruta/a/tu/archivo.bblx")

    // Esta es la función que conectarás con el Composable
    suspend fun fetchVerseContent(bibleId: String, citation: String): String {

        return try {
            val verses = getVerseUseCase(bibleId, citation)

            if (verses.isEmpty()) {
                return DisplayResources.get(lang).bible.verseNotFound
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

    fun scheduleNewQuiz(
        courseId: String,
        quizTemplateId: String,
        date: String,
        details: String? = null,
        onSuccess: () -> Unit
    ) {
        screenModelScope.launch {
            isSchedulingQuiz = true

            // 1. Creamos el objeto de dominio para la solicitud
            val request = ScheduledQuizDomainRequest(
                courseId = courseId,
                quizTemplateId = quizTemplateId,
                quizDate = date,
                details = details,
                status = 1 // 1 = Programado por defecto
            )

            // 2. Invocamos el Use Case
            scheduleQuiz(request).onSuccess { scheduledQuiz ->
                _currentScheduledQuiz.value = scheduledQuiz
                currentScheduledQuizState.value = scheduledQuiz
                println("Examen programado con éxito: ${scheduledQuiz.id}")
                // Podrías añadir lógica aquí para refrescar una lista de "quizzes próximos"
                onSuccess()
            }.onFailure { error ->
                // Manejo de errores (puedes usar un canal de eventos para mostrar un Toast/Snackbar)
                println("Error al programar examen: ${error.message}")
            }

            isSchedulingQuiz = false
        }
    }

    fun getLocalIPAddress(): String? {
        // 1. Obtener todas las interfaces de red del sistema
        val interfaces = NetworkInterface.getNetworkInterfaces().toList()

        for (networkInterface in interfaces) {
            // 2. Filtrar interfaces que no están activas o son loopback (127.0.0.1)
            if (!networkInterface.isUp || networkInterface.isLoopback) continue

            // 3. Iterar sobre todas las direcciones IP asociadas a la interfaz
            for (address in networkInterface.inetAddresses.toList()) {
                // 4. Filtrar solo direcciones IPv4 y que no sean loopback
                if (address is Inet4Address && !address.isLoopbackAddress) {
                    // 5. Devolver la dirección IP encontrada
                    return address.hostAddress
                }
            }
        }
        // Si no se encuentra ninguna dirección adecuada
        return null
    }

    var availableQuizzes by mutableStateOf<List<QuizTemplate>>(emptyList())
        private set

    var isLoadingQuizzes by mutableStateOf(false)
        private set
    private var isQuizzesLoaded = false

    /**
     * Carga los exámenes filtrados por la materia actual
     */
    fun loadQuizzesForSubject(subjectId: String, courseId: String) {
        if (isQuizzesLoaded) return
        screenModelScope.launch {
            isLoadingQuizzes = true
            // En lugar de errorMessage = null, limpiamos el mensaje central
            showUserMessage("")

            getAllQuizTemplates(
                page = 1,
                limit = 50,
                subjectId = subjectId,
                courseId = courseId
            ).onSuccess { pagination ->
                availableQuizzes = pagination.docs
            }.onFailure { exception ->
                availableQuizzes = emptyList()
                handleFailure(exception) // Esto ya llamará a showUserMessage
            }
            isLoadingQuizzes = false
        }
    }

    fun showUserMessage(text: String, error: Boolean = false) {
        isError = error
        _message.value = text
    }
    private fun handleFailure(exception: Throwable): String {
        val errorText = "Error: ${exception.localizedMessage}" // O tu lógica de i18n
        showUserMessage(errorText, error = true)
        return errorText
    }

    fun refreshQuizzes(subjectId: String) {
        isQuizzesLoaded = false
        //loadQuizzesForSubject(subjectId)
    }
    // Estado observable para la UI (usado en WebSocketControls)

    // EN DisplayViewModel.kt

    fun startWebsocketServer() {
        if (isServerRunning.value || !_buttonEnabled.value) return

        // 2. Bloqueamos el botón inmediatamente para evitar el doble clic
        _buttonEnabled.value = false
        isError = false

        // 🔍 VALIDACIÓN DE RED LOCAL
        val ip = getLocalIPAddress()
        if (ip == null || ip == "127.0.0.1") {
            _message.value = DisplayResources.get(lang).errors.noLocalNetwork
            isError = true
            _buttonEnabled.value = true // Re-habilitamos porque falló la validación
            return
        }

        _message.value = DisplayResources.get(lang).messages.serverStarting

        // Solo un try-catch global para errores de creación del hilo
        try {
            websocketThread = thread(isDaemon = true) {
                var portToTry = 8081
                var success = false

                while (!success && portToTry < 8090) {
                    try {
                        val server = embeddedServer(Netty, port = portToTry, host = "0.0.0.0") {
                            install(WebSockets) {
                                pingPeriod = Duration.ofSeconds(15)
                                timeout = Duration.ofSeconds(30)
                            }
                            configureRouting()
                        }

                        // Si llegamos aquí, el puerto parece disponible
                        websocketServer = server
                        currentPort = portToTry
                        success = true

                        // 1. ACTUALIZAR UI DESDE EL HILO (Solo si hay éxito)
                        screenModelScope.launch {
                            _isServerRunning.value = true
                            val ip = getLocalIPAddress() ?: "localhost"
                            _message.value = DisplayResources.get(lang).messages.serverReady(ip)
                            //_message.value = "$ip:$currentPort/display/$currentCourseId"
                            kotlinx.coroutines.delay(500)
                            _buttonEnabled.value = true
                        }

                        server.start(wait = true) // Este bloquea el hilo hasta que se detenga

                    } catch (e: BindException) {
                        websocketServer = null
                        portToTry++
                    } catch (e: Exception) {
                        println("Error fatal en servidor: ${e.message}")
                        break
                    }
                }

                // 2. SI FALLA DESPUÉS DE RECORRER LOS PUERTOS
                if (!success) {
                    screenModelScope.launch {
                        _isServerRunning.value = false
                        _message.value = DisplayResources.get(lang).errors.serverPortError
                        kotlinx.coroutines.delay(500)
                        _buttonEnabled.value = true
                    }
                }
            }
        } catch (e: Exception) {
            // Error al crear el hilo o config básica
            _isServerRunning.value = false
            _buttonEnabled.value = true
            _message.value = "Error inicial: ${e.message}"
        }//finally {
        //    _buttonEnabled.value = true
        //}
        // NOTA: Quitamos el finally de aquí porque la UI se maneja dentro del hilo ahora
    }

    // 4. Lógica de enrutamiento (Reemplaza a tu antiguo Application.module)
    private fun Application.configureRouting() {
        routing {
            // Ruta dinámica que captura el {courseId}
            webSocket("/display/{courseId}") {
                // Obtener el ID de la URL
                val courseId = call.parameters["courseId"]
                val userId = call.request.queryParameters["userId"] ?: "desconocido"
                val clientIp = call.request.origin.remoteHost

                if (courseId != currentCourseId) {
                    println("Conexión rechazada: El alumno buscaba '$courseId' pero el curso activo es '$currentCourseId'")
                    // Cerramos con un código de error personalizado (4003 es un ejemplo de "Policy Violation")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "COURSE_NOT_FOUND"))
                    return@webSocket
                }

                val studentInfo = currentCourseStudents.find { it.id == userId }

                if (studentInfo == null) {
                    println("Acceso denegado: El usuario $userId no está inscrito en este curso.")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "NOT_ENROLLED"))
                    return@webSocket
                }
                // Si lo encuentra, usa el fullName; si no, usa "Invitado" o el ID
                val displayName = studentInfo?.fullName ?: "Estudiante ($userId)"

                val thisConnection = Connection(
                    session = this,
                    userId = userId,
                    name = displayName,
                    address = clientIp,
                    isTakingQuiz = false)

                // Obtener o crear la sala para este curso
                val room = courseConnections.getOrPut(courseId) {
                    Collections.synchronizedSet(LinkedHashSet())
                }

                // Añadir conexión a la sala
                room += thisConnection
                println("Nuevo estudiante en curso $courseId. Total en sala: ${room.size}")
                updateParticipants()

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            println("Mensaje recibido de ${thisConnection.name}: $text")

                            // 🎯 Detectamos el comando que enviamos desde el estudiante
                            if (text.startsWith("STUDENT_START_QUIZ:")) {
                                val room = courseConnections[currentCourseId]
                                if (room != null) {
                                    // 1. Buscamos la conexión vieja
                                    //val oldConnection = thisConnection

                                    // 2. CREAMOS UNA NUEVA REFERENCIA (Copia)
                                    // Esto es vital para que Compose detecte el cambio de objeto
                                    //val updatedConnection = oldConnection.copy(isTakingQuiz = true)
                                   // thisConnection.isTakingQuiz = true
                                    val updatedConnection = thisConnection.copy(isTakingQuiz = true)

                                    // 3. Actualizamos la sala (Set)
                                    room.remove(thisConnection)
                                    room.add(updatedConnection)

                                    // 🎯 IMPORTANTE: Forzamos el update en el Main Thread
                                    screenModelScope.launch(Dispatchers.Main) {
                                        updateParticipants()
                                    }
                                }
                            }

                            // Opcional: Podrías recibir cuando termina
                            if (text.startsWith("STUDENT_FINISH_QUIZ:")) {
                                val currentRoom = courseConnections[currentCourseId]
                                if (currentRoom != null) {
                                    // Buscamos la conexión que tenga nuestro userId para actualizarla a false
                                    val connectionInRoom = currentRoom.find { it.userId == userId }
                                    if (connectionInRoom != null) {
                                        val finishedConnection = connectionInRoom.copy(isTakingQuiz = false)
                                        currentRoom.remove(connectionInRoom)
                                        currentRoom.add(finishedConnection)

                                        screenModelScope.launch(Dispatchers.Main) {
                                            updateParticipants()
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("Error conexión ${thisConnection.name}: ${e.localizedMessage}")
                } finally {
                    // Eliminar conexión de la sala específica
                    room -= thisConnection

                    // Si la sala queda vacía, la limpiamos del mapa para ahorrar memoria
                    if (room.isEmpty()) {
                        courseConnections.remove(courseId)
                    }
                    updateParticipants()
                    println("Estudiante desconectado de $courseId")
                }
            }
        }
    }



    // ⭐️ FUNCIÓN PARA DETENER EL SERVIDOR (CORREGIDA) ⭐️
    suspend fun stopWebsocketServer() {
        // 1. GUARDIA: Si ya está detenido, no hagas nada.
        if (!isServerRunning.value) {
            println("Acción ignorada: El servidor ya está detenido.")
            return
        }

        // 2. Deshabilitar el botón INMEDIATAMENTE
        _buttonEnabled.value = false

        // 3. Usar try...finally
        try {
            if (websocketServer != null) {
                println("Cerrando ${courseConnections.size} conexiones activas...")

                courseConnections.forEach { (courseId, room) ->
                    room.forEach { connection ->
                        try {
                            connection.session.close(CloseReason(CloseReason.Codes.GOING_AWAY, "Server shut down"))
                        } catch (e: Exception) { /* ignore */ }
                    }
                }
                courseConnections.clear() // Limpiar el mapa de salas

                websocketServer!!.stop(500, 2000)
                websocketServer = null

                websocketThread?.interrupt()
                websocketThread = null

                _isServerRunning.value = false
                println("Servidor WebSocket detenido.")

            } else {
                // Si el servidor es nulo, pero isServerRunning era true (estado inconsistente)
                // lo corregimos.
                println("El servidor ya está detenido (chequeo interno).")
                _isServerRunning.value = false
            }
        } catch (e: Exception) {
            println("Error al detener el servidor: ${e.message}")
            // Forzar el estado a 'detenido' en caso de error
            _isServerRunning.value = false
            websocketServer = null
            websocketThread = null
        } finally {
            // 4. RE-HABILITAR SIEMPRE
            kotlinx.coroutines.delay(500)
            _buttonEnabled.value = true
        }
    }

    // ⭐️ FUNCIÓN PARA ENVIAR ACTUALIZACIONES (suspendida) ⭐️
    suspend fun broadcastSlideUpdate(slideNumber: Int) {
        if (!isServerRunning.value || currentCourseId.isBlank()) return

        // Hacemos una copia de la lista de la sala actual para seguridad
        val roomConnections = courseConnections[currentCourseId]?.toList() ?: return

        val messageToBroadcast = "SLIDE_UPDATE:$slideNumber"

        coroutineScope {
            roomConnections.forEach { connection ->
                launch { // Lanzamos cada envío en una corrutina hija para que un fallo no detenga a los demás
                    try {
                        connection.session.send(Frame.Text(messageToBroadcast))
                    } catch (e: Exception) {
                        println("Error enviando a ${connection.name}")
                    }
                }
            }
        }
    }

    // ⭐️ BUENA PRÁCTICA: Detener el servidor cuando el ViewModel se destruye ⭐️
    override fun onDispose() {
        // Lanzamos la función suspendida stopWebsocketServer() en el scope de la ScreenModel
        screenModelScope.launch {
            stopWebsocketServer()
        }
        super.onDispose()
    }


}
