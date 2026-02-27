package com.jaco.cc3d.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.TeacherDisplayScreen
import com.jaco.cc3d.presentation.publico.login.LoginScreen
import com.jaco.cc3d.presentation.publico.login.LoginViewModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.di.Injector
import com.jaco.cc3d.data.repositories.auth.AuthRepositoryImpl
import com.jaco.cc3d.data.repositories.apiAuth.ApiAuthRepositoryImpl
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.TeacherDisplayViewModel
//import com.jaco.cc3d.di.Injector // Importa tu objeto inyector
import com.jaco.cc3d.domain.usecases.bible.GetVerseUseCase
import com.jaco.cc3d.domain.usecases.auth.LoginUseCase
import com.jaco.cc3d.domain.usecases.institute.CreateInstitute
import com.jaco.cc3d.domain.usecases.institute.GetInstitutes
import com.jaco.cc3d.domain.usecases.institute.UpdateInstitute
import com.jaco.cc3d.domain.usecases.institute.DeleteInstitute
import com.jaco.cc3d.domain.usecases.user.CreateUser
import com.jaco.cc3d.domain.usecases.user.DeleteUser
import com.jaco.cc3d.domain.usecases.user.GetUsers
import com.jaco.cc3d.domain.usecases.user.UpdateUser
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.InstitutesViewModel
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.StudentDisplayViewModel
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.users.UsersScreen
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.users.UsersViewModel

import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import cc3d.composeapp.generated.resources.Res
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.domain.usecases.course.CreateCourse
import com.jaco.cc3d.domain.usecases.course.DeleteCourse
import com.jaco.cc3d.domain.usecases.course.GetCourses
import com.jaco.cc3d.domain.usecases.course.GetCoursesByTeacher
import com.jaco.cc3d.domain.usecases.course.UpdateCourse
import com.jaco.cc3d.domain.usecases.enrollment.CreateEnrollment
import com.jaco.cc3d.domain.usecases.enrollment.DeleteEnrollment
import com.jaco.cc3d.domain.usecases.enrollment.GetAllEnrollments
import com.jaco.cc3d.domain.usecases.enrollment.GetStudentEnrollments
import com.jaco.cc3d.domain.usecases.quizAttempt.CreateAttempt
import com.jaco.cc3d.domain.usecases.quizAttempt.SubmitQuiz
import com.jaco.cc3d.domain.usecases.quizQuestion.CreateQuizQuestion
import com.jaco.cc3d.domain.usecases.quizQuestion.DeleteQuizQuestion
import com.jaco.cc3d.domain.usecases.quizQuestion.GetAllQuizQuestions
import com.jaco.cc3d.domain.usecases.quizQuestion.UpdateQuizQuestion
import com.jaco.cc3d.domain.usecases.quizTemplate.CreateQuizTemplate
import com.jaco.cc3d.domain.usecases.quizTemplate.DeleteQuizTemplate
import com.jaco.cc3d.domain.usecases.quizTemplate.GetAllQuizTemplates
import com.jaco.cc3d.domain.usecases.quizTemplate.UpdateQuizTemplate
import com.jaco.cc3d.domain.usecases.scheduleQuiz.GetScheduledQuizzesByCourseUseCase
import com.jaco.cc3d.domain.usecases.scheduleQuiz.ScheduleQuiz
import com.jaco.cc3d.domain.usecases.subject.CreateSubject
import com.jaco.cc3d.domain.usecases.subject.DeleteSubject
import com.jaco.cc3d.domain.usecases.subject.GetSubjects
import com.jaco.cc3d.domain.usecases.subject.UpdateSubject
import com.jaco.cc3d.domain.usecases.subjectConfig.SaveSubjectConfig
import com.jaco.cc3d.domain.usecases.subjectConfig.SyncSubjectContent
import com.jaco.cc3d.domain.usecases.subjectConfig.UploadSubjectContent
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.DashboardSection
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.PrivateDashboardScreen
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses.CoursesScreen
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses.CoursesViewModel
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments.EnrollmentScreen
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments.EnrollmentViewModel
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions.QuizQuestionScreen
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions.QuizQuestionViewModel
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.SubjectsStore
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.SubjectsViewModel
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.users.TeachersStore

// << NUEVAS IMPORTACIONES PARA QUIZ TEMPLATES >>
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates.QuizTemplateScreen
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates.QuizTemplateViewModel
import com.jaco.cc3d.presentation.privado.studentDashboard.StudentDashboardViewModel
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.StudentDisplayScreen
import com.jaco.cc3d.presentation.privado.teacherDashboard.TeacherDashboardViewModel
import kotlinx.coroutines.launch

import cafe.adriel.voyager.navigator.Navigator
import com.jaco.cc3d.domain.usecases.course.GetCourseById
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Asumo que tienes use cases para QuizTemplate en el dominio



object Login : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        //val viewModel: LoginViewModel = getScreenModel()
        val viewModel: LoginViewModel = rememberScreenModel {
            // Creamos manualmente el ViewModel usando la dependencia que Dagger provee
            val backendRepository: ApiAuthRepositoryImpl = Injector.backendRepository
            val authRepository: AuthRepositoryImpl = Injector.authRepository
            val encryptedDesktopTokenManager: EncryptedDesktopTokenManager = Injector.encryptedDesktopTokenManager
            val userDao = Injector.appDatabase.userDao()
            val loginUseCase = LoginUseCase(authRepository, backendRepository, encryptedDesktopTokenManager,userDao)
            // 2. Pasamos la dependencia al constructor
            LoginViewModel(loginUseCase, encryptedDesktopTokenManager)
        }
        LoginScreen(
            viewModel = viewModel,
            navigateToTeacherDashboard = {
                navigator.replace(PrivateDashboard(initialTab = DashboardSection.TEACHER_VIEW))  },
            //navigateToStudentDisplay = { navigator.push(StudentDisplay)  },
            navigateToStudentDisplay = {
                navigator.replace(PrivateDashboard(initialTab = DashboardSection.STUDENT_VIEW))
            },
            //navigateToUsers = { navigator.push(Users)  },
            //navigateToInstitute = { navigator.push(Institutes)  },
            navigateToPrivateDashboard = {
                // Para el Admin (Rol 2)
                navigator.replace(PrivateDashboard(initialTab = DashboardSection.INSTITUTES))
            },
            //navigateToPrivateDashboard = { navigator.replace(PrivateDashboard())  },
        )
    }
}


data class Users(val instituteId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tokenManager = Injector.encryptedDesktopTokenManager
        //val viewModel: LoginViewModel = getScreenModel()
        val viewModel: UsersViewModel = rememberScreenModel {
            val userRepository = Injector.userRepository

            // 2. Inicializar los Use Cases de Usuario, inyectándoles el repositorio
            val createUser = CreateUser(userRepository)
            val getUsers = GetUsers(userRepository)
            val updateUser = UpdateUser(userRepository)
            val deleteUser = DeleteUser(userRepository)

            // 3. Pasar las dependencias al constructor del ViewModel
            UsersViewModel(
                getUsersUseCase = getUsers,
                createUserUseCase = createUser,
                updateUserUseCase = updateUser,
                deleteUserUseCase = deleteUser
            )
        }
        LaunchedEffect(Unit) {
            viewModel.setInstituteId(instituteId)
        }
        val scope = rememberCoroutineScope()
        UsersScreen(
            viewModel = viewModel,
            onLogout = {
                navigator.performFullLogout(scope, tokenManager)
            },
            onBack = {
                navigator.pop() // Voyager saca la pantalla actual del stack
            },
            instituteId = instituteId
        )
    }
}

data class Courses(val instituteId: String, val instituteName: String, val subjectsStore: SubjectsStore, val teachersStore: TeachersStore) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tokenManager = Injector.encryptedDesktopTokenManager

        // 1. Inyección y creación del ViewModel de Cursos
        val viewModel: CoursesViewModel = rememberScreenModel {
            val courseRepository = Injector.courseRepository

            // 2. Inicializar los Use Cases de Curso
            val createCourse = CreateCourse(courseRepository)
            val getCourses = GetCourses(courseRepository)
            val updateCourse = UpdateCourse(courseRepository)
            val deleteCourse = DeleteCourse(courseRepository)

            // 3. Pasar las dependencias al constructor del ViewModel
            CoursesViewModel(
                getCoursesUseCase = getCourses,
                createCourseUseCase = createCourse,
                updateCourseUseCase = updateCourse,
                deleteCourseUseCase = deleteCourse,
                subjectsStore = subjectsStore, // 👈 ¡Inyección correcta!
                teachersStore = teachersStore
            )
        }

        // 4. Establecer el ID del Instituto en el ViewModel
        LaunchedEffect(Unit) {
            viewModel.initializeInstituteFilter(instituteId, instituteName)
        }

        val navigateToEnrollment = { course: Course ->
            // Intentamos obtener el nombre de la materia del Store para el título
            // Si no está cargado, usamos el Grupo como fallback
            val subjectName = subjectsStore.subjects.find { it.id == course.subjectId }?.name ?: "Grupo ${course.group}"

            navigator.push(
                Enrollment(
                    courseId = course.id,
                    courseName = subjectName,
                    instituteId = instituteId
                )
            )
        }
        val scope = rememberCoroutineScope()
        // 5. Llamada a la Composable de la pantalla de Cursos
        CoursesScreen(
            viewModel = viewModel,
            onLogout = {
                navigator.performFullLogout(scope, tokenManager)
            },
            onBack = {
                navigator.pop()
            },
            instituteId = instituteId,
            instituteName = instituteName,
            navigateToEnrollment = navigateToEnrollment
        )
    }
}

data class Questions(
    val templateId: String,
    val templateName: String,
    val language: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tokenManager = Injector.encryptedDesktopTokenManager

        // 1. Inyección y creación del ViewModel de Preguntas
        val viewModel: QuizQuestionViewModel = rememberScreenModel {
            // Obtenemos el repositorio del inyector
            val repository = Injector.quizQuestionRepository

            // 2. Inicializar los Use Cases de QuizQuestion
            val getAllQuizQuestions = GetAllQuizQuestions(repository)
            val createQuizQuestion = CreateQuizQuestion(repository)
            val updateQuizQuestion = UpdateQuizQuestion(repository)
            val deleteQuizQuestion = DeleteQuizQuestion(repository)

            // 3. Crear el ViewModel con sus dependencias
            QuizQuestionViewModel(
                getAllUseCase = getAllQuizQuestions,
                createUseCase = createQuizQuestion,
                updateUseCase = updateQuizQuestion,
                deleteUseCase = deleteQuizQuestion
            )
        }

        // 4. Establecer el contexto de la plantilla actual
        LaunchedEffect(templateId) {
            viewModel.setTemplateContext(templateId, language)
        }
        val scope = rememberCoroutineScope()
        // 5. Llamada a la pantalla (Screen)
        QuizQuestionScreen(
            viewModel = viewModel,
            templateId = templateId,
            templateName = templateName,
            language = language,
            onBack = {
                navigator.pop()
            },
            onLogout = {
                navigator.performFullLogout(scope, tokenManager)
            }
        )
    }
}
// << NUEVO: Objeto Screen para QuizTemplate >>
data class QuizTemplate(
    val subjectId: String,
    val subjectName: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tokenManager = Injector.encryptedDesktopTokenManager

        // 1. Inyección y creación del ViewModel
        val viewModel: QuizTemplateViewModel = rememberScreenModel {
            // Asumo que el Injector proporciona el repositorio
            val quizTemplateRepository = Injector.quizTemplateRepository

            // 2. Inicializar los Use Cases de QuizTemplate
            val getAllQuizTemplates = GetAllQuizTemplates(quizTemplateRepository)
            val createQuizTemplate = CreateQuizTemplate(quizTemplateRepository)
            val updateQuizTemplate = UpdateQuizTemplate(quizTemplateRepository)
            val deleteQuizTemplate = DeleteQuizTemplate(quizTemplateRepository)

            QuizTemplateViewModel(
                getAllQuizTemplatesUseCase = getAllQuizTemplates,
                createQuizTemplateUseCase = createQuizTemplate,
                updateQuizTemplateUseCase = updateQuizTemplate,
                deleteQuizTemplateUseCase = deleteQuizTemplate
            )
        }

        LaunchedEffect(Unit) {
            viewModel.setSubjectContext(subjectId) // Asumiendo que tu ViewModel tiene un método initialize
        }

        val onNavigateToQuestions: (com.jaco.cc3d.domain.models.QuizTemplate) -> Unit = { template ->
            navigator.push(
                Questions(
                    templateId = template.id,
                    templateName = template.name,
                    language = template.language
                )
            )
        }

        val scope = rememberCoroutineScope()
        QuizTemplateScreen(
            viewModel = viewModel,
            onBack = { navigator.replace(PrivateDashboard(initialTab = DashboardSection.SUBJECTS)) },
            onLogout = { navigator.performFullLogout(scope, tokenManager) },
            subjectId = subjectId,
            subjectName = subjectName,
            onNavigateToQuestions = onNavigateToQuestions
        )
    }
}

//enum class DashboardTab { INSTITUTES, SUBJECTS }
@OptIn(ExperimentalMaterial3Api::class)
data class PrivateDashboard(val initialTab: DashboardSection = DashboardSection.INSTITUTES) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tokenManager = Injector.encryptedDesktopTokenManager


        // -----------------------------------------------------------
        // 1. DEPENDENCIAS BASE (Repositorios y Use Case de Lectura)
        // -----------------------------------------------------------
        val subjectRepository = Injector.subjectRepository
        val getSubjects = GetSubjects(subjectRepository)
        val userRepository = Injector.userRepository // 💡 Asumido

        val getUsers = GetUsers(userRepository)


        // -----------------------------------------------------------
        // 2. 💡 CREACIÓN DEL STORE SINGLETON (SubjectsStore)
        // -----------------------------------------------------------
        val subjectsStore: SubjectsStore = rememberScreenModel {
            SubjectsStore(getSubjects)
        }
        val teachersStore: TeachersStore = rememberScreenModel {
            TeachersStore(getUsers)
        }

        val displayViewModel: TeacherDisplayViewModel = rememberScreenModel {
            val bibleRepository = Injector.bibleRepository
            val getVerseUseCase = GetVerseUseCase(bibleRepository)
            val quizTemplateRepository = Injector.quizTemplateRepository
            val getAllQuizTemplates = GetAllQuizTemplates(quizTemplateRepository)
            val scheduledQuizRepository = Injector.scheduledQuizRepository
            val scheduleQuiz = ScheduleQuiz(scheduledQuizRepository)
            val courseRepository = Injector.courseRepository
            val getCourseById = GetCourseById(courseRepository)
            TeacherDisplayViewModel(getVerseUseCase, getAllQuizTemplates, scheduleQuiz, getCourseById)
        }

        val studentDashboardViewModel: StudentDashboardViewModel = rememberScreenModel {
            val enrollmentRepository = Injector.enrollmentRepository
            //val tokenManager = Injector.encryptedDesktopTokenManager
            val enrollmentDao = Injector.appDatabase.enrollmentDao()
            // Creamos el Use Case específico
            val getStudentEnrollments = GetStudentEnrollments(enrollmentRepository, enrollmentDao)

            StudentDashboardViewModel(
                getStudentEnrollments = getStudentEnrollments,
                tokenManager = tokenManager
            )
        }
        val studentViewModel: StudentDisplayViewModel = rememberScreenModel {
            val bibleRepository = Injector.bibleRepository
            val scheduledQuizRepository = Injector.scheduledQuizRepository
            val quizQuestionRepository = Injector.quizQuestionRepository
            val quizAttemptRepository = Injector.studentQuizAttemptRepository
            val tokenManager = Injector.encryptedDesktopTokenManager
            val getVerseUseCase = GetVerseUseCase(bibleRepository)
            val getScheduledQuizzesUseCase = GetScheduledQuizzesByCourseUseCase(scheduledQuizRepository)
            val getAllQuizQuestionsUseCase = GetAllQuizQuestions(quizQuestionRepository)

            val createAttemptUseCase = CreateAttempt(quizAttemptRepository)
            val submitQuizUseCase = SubmitQuiz(quizAttemptRepository)

            StudentDisplayViewModel(
                getVerseUseCase,
                getScheduledQuizzesUseCase,
                getAllQuizQuestionsUseCase,
                createAttemptUseCase, // Inyectado
                submitQuizUseCase,     // Inyectado
                tokenManager
            )
        }

        val teacherDashboardViewModel: TeacherDashboardViewModel = rememberScreenModel {
            val courseRepository = Injector.courseRepository
            // Creamos el nuevo Use Case que hicimos antes
            val getCoursesByTeacher = GetCoursesByTeacher(courseRepository)

            TeacherDashboardViewModel(
                getCoursesByTeacher = getCoursesByTeacher,
                tokenManager = tokenManager
            )
        }

        val loginViewModel: LoginViewModel = rememberScreenModel {
            val backendRepository = Injector.backendRepository
            val authRepository = Injector.authRepository
            val tokenManager = Injector.encryptedDesktopTokenManager
            val userDao = Injector.appDatabase.userDao()
            val loginUseCase = LoginUseCase(authRepository, backendRepository, tokenManager, userDao)
            LoginViewModel(loginUseCase, tokenManager)
        }
        // --- 1. Inyección de InstitutesViewModel ---
        val institutesViewModel: InstitutesViewModel = rememberScreenModel {
            val instituteRepository = Injector.instituteRepository
            val createInstitute = CreateInstitute(instituteRepository)
            val getInstitutes = GetInstitutes(instituteRepository)
            val updateInstitute = UpdateInstitute(instituteRepository)
            val deleteInstitute = DeleteInstitute(instituteRepository)
            InstitutesViewModel(getInstitutes, createInstitute, updateInstitute, deleteInstitute)
        }

        // --- 2. 💡 Inyección de SubjectsViewModel ---
        val subjectsViewModel: SubjectsViewModel = rememberScreenModel {
            val subjectRepository = Injector.subjectRepository // Se asume que SubjectRepository está inyectado
            val subjectConfigRepository = Injector.subjectConfigRepository
            val cloudinaryRepository = Injector.cloudinaryRepository

            val createSubject = CreateSubject(subjectRepository)
            val getSubjects = GetSubjects(subjectRepository)
            val updateSubject = UpdateSubject(subjectRepository)
            val deleteSubject = DeleteSubject(subjectRepository)

            val uploadSubjectContent = UploadSubjectContent(cloudinaryRepository)
            val saveSubjectConfig = SaveSubjectConfig(subjectConfigRepository)

            // 4. El Orquestador que pediste
            val syncSubjectContent = SyncSubjectContent(
                uploadUseCase = uploadSubjectContent,
                saveConfigUseCase = saveSubjectConfig
            )

            SubjectsViewModel(getSubjects, createSubject, updateSubject, deleteSubject, subjectsStore, syncSubjectContent)
        }



        // 💡 LÓGICA DE CAMBIO DE ROL: Redirige según el ID del rol seleccionado
        val onRoleSwitched: (Int) -> Unit = { roleId ->
            when (roleId) {
                2 -> {
                    // Se queda en el Dashboard, vuelve a Institutos
                    navigator.replace(PrivateDashboard(initialTab = DashboardSection.INSTITUTES))
                }
                1 -> {
                    // 💡 EL PROFESOR SE VA A SU PROPIA PANTALLA (Fuera del Dashboard)
                    navigator.replace(PrivateDashboard(initialTab = DashboardSection.TEACHER_VIEW))
                }
                0 -> {
                    // El Estudiante se queda en el Dashboard pero en su sección
                    navigator.replace(PrivateDashboard(initialTab = DashboardSection.STUDENT_VIEW))
                }
            }
        }
        val navigateToUsers = { instituteId: String -> navigator.push(Users(instituteId)) }

        val navigateToCourses = { instituteId: String, instituteName: String ->
            navigator.push(
                Courses(
                    instituteId = instituteId,
                    instituteName = instituteName, // 💡 Pasamos el nombre
                    subjectsStore = subjectsStore,
                    teachersStore = teachersStore
                )
            )
        }

        // << NUEVO: Callback de Navegación a Quiz Templates >>
        val navigateToQuizTemplates = { subjectId: String, subjectName: String ->
            navigator.push(
                QuizTemplate(
                    subjectId = subjectId,
                    subjectName = subjectName
                )
            )
        }

        val navigateToTeacherDisplay = { courseId: String, subjectName: String, subjectId: String, contentUrl: String ->
            navigator.push(
                TeacherDisplay(courseId, subjectName, subjectId, contentUrl)
            )
        }

        val navigateToStudentDisplay = { courseId: String, subjectName: String, subjectId: String, contentUrl: String ->
            navigator.push(StudentDisplay(courseId, subjectName, subjectId, contentUrl ))
        }

        val scope = rememberCoroutineScope()
        // 💡 LLAMADA AL COMPOSABLE DE PANTALLA
        PrivateDashboardScreen(
            initialTab = initialTab,
            institutesViewModel = institutesViewModel,
            subjectsViewModel = subjectsViewModel,
            studentDashboardViewModel = studentDashboardViewModel,
            teacherDashboardViewModel = teacherDashboardViewModel,
            studentViewModel = studentViewModel,
            displayViewModel = displayViewModel,
            loginViewModel = loginViewModel,
            onLogout = {
                navigator.performFullLogout(scope, tokenManager)
            },
            onRoleSwitched = onRoleSwitched,
            navigateToUsers = navigateToUsers,
            navigateToCourses = navigateToCourses,
            navigateToTeacherDisplay = navigateToTeacherDisplay,
            navigateToStudentDisplay = navigateToStudentDisplay,
            // << NUEVO: Pasar el callback de navegación a Quiz Templates >>
            navigateToQuizTemplates = navigateToQuizTemplates
        )
    }
}

data class Enrollment(
    val courseId: String,
    val courseName: String, // Para mostrar en el título (Ej: "Matemáticas - Grupo A")
    val instituteId: String // Necesario para buscar estudiantes del instituto
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tokenManager = Injector.encryptedDesktopTokenManager

        val viewModel: EnrollmentViewModel = rememberScreenModel {
            // 1. Obtener Repositorios
            // ⚠️ Asegúrate de añadir enrollmentRepository a tu Injector si no está
            // val enrollmentRepository = Injector.enrollmentRepository
            // Si no lo tienes en Injector, puedes instanciarlo aquí temporalmente:
            val enrollmentRepository = Injector.enrollmentRepository //EnrollmentRepositoryImpl(enrollmentService)

            val userRepository = Injector.userRepository

            // 2. Crear Use Cases
            val getEnrollments = GetAllEnrollments(enrollmentRepository)
            val createEnrollment = CreateEnrollment(enrollmentRepository)
            val deleteEnrollment = DeleteEnrollment(enrollmentRepository)
            val getUsers = GetUsers(userRepository)

            // 3. Instanciar ViewModel
            EnrollmentViewModel(
                getEnrollmentsUseCase = getEnrollments,
                createEnrollmentUseCase = createEnrollment,
                deleteEnrollmentUseCase = deleteEnrollment,
                getUsersUseCase = getUsers
            )
        }


        // Inicializar con los datos de navegación
        LaunchedEffect(courseId, instituteId) {
            viewModel.initialize(courseId, instituteId)
        }
        val scope = rememberCoroutineScope()

        EnrollmentScreen(
            viewModel = viewModel,
            onLogout = {
                navigator.performFullLogout(scope, tokenManager)
            },
            onBack = { navigator.pop() },
            courseName = courseName
        )
    }
}

data class TeacherDisplay(
    val courseId: String,
    val subjectName: String,
    val subjectId: String,
    val contentUrl: String,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: TeacherDisplayViewModel = rememberScreenModel {
            val bibleRepository = Injector.bibleRepository
            val getVerseUseCase = GetVerseUseCase(bibleRepository)

            val quizTemplateRepository = Injector.quizTemplateRepository
            val getAllQuizTemplates = GetAllQuizTemplates(quizTemplateRepository)

            val scheduledQuizRepository = Injector.scheduledQuizRepository
            val scheduleQuiz = ScheduleQuiz(scheduledQuizRepository)
            val courseRepository = Injector.courseRepository
            val getCourseById = GetCourseById(courseRepository)

            TeacherDisplayViewModel(getVerseUseCase, getAllQuizTemplates, scheduleQuiz, getCourseById)
        }

        TeacherDisplayScreen(
            viewModel = viewModel,
            courseId = courseId, // 👈 Pasamos el ID al Composable
            subjectId = subjectId,
            subjectName = subjectName,
            contentUrl = contentUrl,
            onBack = { navigator.pop() }
        )
    }
}

// Cambia la definición de StudentDisplay o agrégala si no estaba:
data class StudentDisplay(
    val courseId: String,
    val subjectName: String,
    val subjectId: String,
    val contentUrl: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Obtenemos el ViewModel (Voyager se encarga de mantenerlo)
        val viewModel: StudentDisplayViewModel = rememberScreenModel {
            val bibleRepository = Injector.bibleRepository
            val scheduledQuizRepository = Injector.scheduledQuizRepository
            val quizQuestionRepository = Injector.quizQuestionRepository
            val quizAttemptRepository = Injector.studentQuizAttemptRepository
            val tokenManager = Injector.encryptedDesktopTokenManager
            val getVerseUseCase = GetVerseUseCase(bibleRepository)
            val getScheduledQuizzesUseCase = GetScheduledQuizzesByCourseUseCase(scheduledQuizRepository)
            val getAllQuizQuestionsUseCase = GetAllQuizQuestions(quizQuestionRepository)
            val createAttemptUseCase = CreateAttempt(quizAttemptRepository)
            val submitQuizUseCase = SubmitQuiz(quizAttemptRepository)
            StudentDisplayViewModel(
                getVerseUseCase,
                getScheduledQuizzesUseCase,
                getAllQuizQuestionsUseCase,
                createAttemptUseCase,
                submitQuizUseCase,
                tokenManager)
        }

        // Llamamos a la pantalla pasando los nuevos parámetros
        StudentDisplayScreen(
            viewModel = viewModel,
            courseId = courseId,
            subjectId = subjectId,
            subjectName = subjectName,
            contentUrl = contentUrl,
            onBack = { navigator.pop() }
        )
    }
}

fun Navigator.performFullLogout(
    scope: CoroutineScope,
    tokenManager: EncryptedDesktopTokenManager
) {
    scope.launch {
        tokenManager.logout {
            // Llamamos a la limpieza centralizada del Injector
            Injector.clearAllLocalData()
        }
        // Navegamos al inicio
        this@performFullLogout.replaceAll(Login)
    }
}