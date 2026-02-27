package com.jaco.cc3d.di

import com.jaco.cc3d.data.local.AppDatabase
import com.jaco.cc3d.data.local.dao.CourseDao
import com.jaco.cc3d.data.local.dao.UserDao
import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.data.repositories.auth.AuthRepositoryImpl
import com.jaco.cc3d.domain.repositories.bible.BibleRepository
import com.jaco.cc3d.di.DaggerAppComponent
import com.jaco.cc3d.data.repositories.apiAuth.ApiAuthRepositoryImpl
import com.jaco.cc3d.data.repositories.cloudinary.CloudinaryRepositoryImpl
import com.jaco.cc3d.data.repositories.course.CourseRepositoryImpl
import com.jaco.cc3d.data.repositories.enrollment.EnrollmentRepositoryImpl
import com.jaco.cc3d.data.repositories.institute.InstituteRepositoryImpl
import com.jaco.cc3d.data.repositories.quizAttempt.StudentQuizAttemptRepositoryImpl
import com.jaco.cc3d.data.repositories.quizQuestion.QuizQuestionRepositoryImpl
import com.jaco.cc3d.data.repositories.quizTemplate.QuizTemplateRepositoryImpl
import com.jaco.cc3d.data.repositories.scheduledQuiz.ScheduledQuizRepositoryImpl
import com.jaco.cc3d.data.repositories.subject.SubjectRepositoryImpl
import com.jaco.cc3d.data.repositories.subjectConfig.SubjectConfigRepositoryImpl
import com.jaco.cc3d.data.repositories.user.UserRepositoryImpl
import com.jaco.cc3d.domain.usecases.subjectConfig.SyncSubjectContent

/**
 * 🔑 Objeto Injector: Maneja la inyección manual de dependencias Dagger
 * para entornos que no soportan Hilt/Anvil.
 * Nota: DaggerAppComponent debe ser generado por KAPT.
 */
object Injector {
    // 1. Inicializa el Componente Dagger de forma perezosa
    val appComponent: AppComponent by lazy {
        // Asume que DaggerAppComponent fue generado por KAPT
        DaggerAppComponent.create()
    }
    val appDatabase: AppDatabase get() = appComponent.appDatabase()

    suspend fun clearAllLocalData() {
        appDatabase.enrollmentDao().clearAll()
        appDatabase.courseDao().clearAll()
        appDatabase.userDao().clearAll()
        // Aquí podrías añadir: appDatabase.clearAllTables() si logras que compile,
        // o simplemente seguir listando los DAOs.
    }

    // 🔑 2. EXPONER LAS DEPENDENCIAS
    val bibleRepository: BibleRepository get() = appComponent.bibleRepository()
    val authRepository: AuthRepositoryImpl get() = appComponent.authRepository()
    //val authRepository: AuthRepository get() = appComponent.
    val bibleFileManager: BibleFileManager get() = appComponent.bibleFileManager()

    val backendRepository: ApiAuthRepositoryImpl get() = appComponent.apiAuthRepository()
    val instituteRepository: InstituteRepositoryImpl get() = appComponent.instituteRepository()

    val userRepository: UserRepositoryImpl get() = appComponent.userRepository()
    val subjectRepository: SubjectRepositoryImpl get() = appComponent.subjectRepository()
    val courseRepository: CourseRepositoryImpl get() = appComponent.courseRepository()
    val enrollmentRepository: EnrollmentRepositoryImpl get() = appComponent.enrollmentRepository()
    val quizTemplateRepository: QuizTemplateRepositoryImpl get() = appComponent.quizTemplateRepository()
    val quizQuestionRepository: QuizQuestionRepositoryImpl get() = appComponent.quizQuestionRepository()
    val scheduledQuizRepository: ScheduledQuizRepositoryImpl get() = appComponent.scheduledQuizRepository()
    val studentQuizAttemptRepository: StudentQuizAttemptRepositoryImpl get() = appComponent.studentQuizAttemptRepository()
    val subjectConfigRepository: SubjectConfigRepositoryImpl get() = appComponent.subjectConfigRepository()
    val cloudinaryRepository: CloudinaryRepositoryImpl get() = appComponent.cloudinaryRepository()
    val syncSubjectContent: SyncSubjectContent get() = appComponent.syncSubjectContent()

    val encryptedDesktopTokenManager: EncryptedDesktopTokenManager get() = appComponent.encryptedDesktopTokenManager()
    // Opcional: Si el DAO ya no se necesita en ningún Screen/ViewModel, puedes eliminar bibleDao
    // val bibleDao: BibleDao get() = appComponent.bibleDao() // Se puede eliminar si no se usa
}
