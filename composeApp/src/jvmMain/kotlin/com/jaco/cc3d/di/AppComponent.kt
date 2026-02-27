package com.jaco.cc3d.di

import com.jaco.cc3d.data.local.AppDatabase
import com.jaco.cc3d.data.local.dao.CourseDao
import com.jaco.cc3d.data.local.dao.EnrollmentDao
import com.jaco.cc3d.data.local.dao.UserDao
import dagger.Component
import javax.inject.Singleton
import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.data.repositories.auth.AuthRepositoryImpl
import com.jaco.cc3d.data.repositories.bible.BibleRepositoryImpl
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
 * 🔑 AppComponent: El punto de entrada Singleton para tu aplicación JVM Desktop.
 * * Le dice a Dagger qué Módulos debe cargar para construir el grafo.
 */
@Singleton
@Component(modules = [
    BibleModule::class,
    AuthModule::class,
    DatabaseModule::class,
    NetworkModule::class,
])
interface AppComponent {

    //fun courseDao(): CourseDao
    //fun userDao(): UserDao
    //fun enrollmentDao(): EnrollmentDao
    fun appDatabase(): AppDatabase

    fun bibleRepository(): BibleRepositoryImpl // <-- ¡Añadido!

    fun authRepository(): AuthRepositoryImpl
    fun bibleFileManager(): BibleFileManager
    fun apiAuthRepository(): ApiAuthRepositoryImpl
    fun instituteRepository(): InstituteRepositoryImpl
    fun subjectRepository(): SubjectRepositoryImpl
    fun userRepository(): UserRepositoryImpl
    fun courseRepository(): CourseRepositoryImpl
    fun enrollmentRepository(): EnrollmentRepositoryImpl
    fun quizTemplateRepository(): QuizTemplateRepositoryImpl
    fun quizQuestionRepository(): QuizQuestionRepositoryImpl
    fun scheduledQuizRepository(): ScheduledQuizRepositoryImpl
    fun studentQuizAttemptRepository(): StudentQuizAttemptRepositoryImpl
    fun subjectConfigRepository(): SubjectConfigRepositoryImpl
    fun cloudinaryRepository(): CloudinaryRepositoryImpl
    fun syncSubjectContent(): SyncSubjectContent

    fun encryptedDesktopTokenManager(): EncryptedDesktopTokenManager
}