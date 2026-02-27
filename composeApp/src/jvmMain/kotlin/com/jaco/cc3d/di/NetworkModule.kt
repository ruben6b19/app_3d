package com.jaco.cc3d.di

import com.jaco.cc3d.data.local.dao.CourseDao
import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.data.network.apiAuth.ApiAuthService
import com.jaco.cc3d.data.network.course.CourseService
import com.jaco.cc3d.data.network.enrollment.EnrollmentService
import com.jaco.cc3d.data.network.institute.InstituteService
import com.jaco.cc3d.data.network.interceptor.AuthInterceptor
import com.jaco.cc3d.data.network.quizAttempt.StudentQuizAttemptService
import com.jaco.cc3d.data.network.quizQuestion.QuizQuestionService
import com.jaco.cc3d.data.network.quizTemplate.QuizTemplateService
import com.jaco.cc3d.data.network.scheduledQuiz.ScheduledQuizService
import com.jaco.cc3d.data.network.subject.SubjectService
import com.jaco.cc3d.data.network.subjectConfig.SubjectConfigService
import com.jaco.cc3d.data.network.user.UserService
import com.jaco.cc3d.data.remote.cloudinary.CloudinaryService
import com.jaco.cc3d.domain.repositories.apiAuth.ApiAuthRepository
import com.jaco.cc3d.data.repositories.apiAuth.ApiAuthRepositoryImpl
import com.jaco.cc3d.data.repositories.cloudinary.CloudinaryRepositoryImpl
import com.jaco.cc3d.data.repositories.course.CourseRepositoryImpl
import com.jaco.cc3d.data.repositories.enrollment.EnrollmentRepositoryImpl
import com.jaco.cc3d.domain.repositories.institute.InstituteRepository
import com.jaco.cc3d.data.repositories.institute.InstituteRepositoryImpl
import com.jaco.cc3d.data.repositories.quizAttempt.StudentQuizAttemptRepositoryImpl
import com.jaco.cc3d.data.repositories.quizQuestion.QuizQuestionRepositoryImpl
import com.jaco.cc3d.data.repositories.quizTemplate.QuizTemplateRepositoryImpl
import com.jaco.cc3d.data.repositories.scheduledQuiz.ScheduledQuizRepositoryImpl
import com.jaco.cc3d.data.repositories.subject.SubjectRepositoryImpl
import com.jaco.cc3d.data.repositories.subjectConfig.SubjectConfigRepositoryImpl
import com.jaco.cc3d.domain.repositories.user.UserRepository
import com.jaco.cc3d.data.repositories.user.UserRepositoryImpl
import com.jaco.cc3d.domain.repositories.cloudinary.CloudinaryRepository
import com.jaco.cc3d.domain.repositories.course.CourseRepository
import com.jaco.cc3d.domain.repositories.enrollment.EnrollmentRepository
import com.jaco.cc3d.domain.repositories.quizAttempt.StudentQuizAttemptRepository
import com.jaco.cc3d.domain.repositories.quizQuestion.QuizQuestionRepository
import com.jaco.cc3d.domain.repositories.quizTemplate.QuizTemplateRepository
import com.jaco.cc3d.domain.repositories.scheduledQuiz.ScheduledQuizRepository
import com.jaco.cc3d.domain.repositories.subject.SubjectRepository
import com.jaco.cc3d.domain.repositories.subjectConfig.SubjectConfigRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object NetworkModule {
    private const val BASE_URL = "https://service-api-henna.vercel.app/api/v1/"
    //private const val BASE_URL = "http://localhost:8000/api/v1/"

    @Provides
    @Singleton
    fun provideContentType(): MediaType {
        // Usamos MediaType.parse() como fallback, ya que fue la opción que te funcionó
        return "application/json".toMediaType()
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        // Dagger inyecta el interceptor ya que tiene un constructor @Inject
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // <--- AQUÍ SE AÑADE EL INTERCEPTOR DE AUTENTICACIÓN
            .addInterceptor(authInterceptor)
            // Opcional: puedes añadir más interceptores aquí, como uno para logging
            // .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(contentType: MediaType, json: Json, client: OkHttpClient): Retrofit {
        // Configuramos Retrofit, inyectando las dependencias que proveemos arriba
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }


    @Provides
    @Singleton
    fun provideApiAuthService(retrofit: Retrofit): ApiAuthService {
        // Dagger crea el servicio a partir de la instancia de Retrofit
        return retrofit.create(ApiAuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiAuthRepository(
        apiAuthService: ApiAuthService,
        tokenManager: EncryptedDesktopTokenManager
    ): ApiAuthRepository {
        return ApiAuthRepositoryImpl(apiAuthService, tokenManager)
    }

    @Provides
    @Singleton
    fun provideInstituteService(retrofit: Retrofit): InstituteService {
        // Dagger crea el servicio a partir de la instancia de Retrofit
        return retrofit.create(InstituteService::class.java)
    }

    @Provides
    @Singleton
    fun provideInstituteRepository(
        instituteService: InstituteService,
    ): InstituteRepository {
        return InstituteRepositoryImpl(instituteService)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        // Dagger crea el servicio a partir de la instancia de Retrofit
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userService: UserService,
    ): UserRepository {
        return UserRepositoryImpl(userService)
    }

    @Provides
    @Singleton
    fun provideSubjectService(retrofit: Retrofit): SubjectService {
        return retrofit.create(SubjectService::class.java)
    }

    @Provides
    @Singleton
    fun provideSubjectRepository(
        subjectService: SubjectService,
    ): SubjectRepository { // Retorna la INTERFAZ (Contrato)
        return SubjectRepositoryImpl(subjectService) // Retorna la IMPLEMENTACIÓN (Data)
    }

    @Provides
    @Singleton
    fun provideCourseService(retrofit: Retrofit): CourseService {
        return retrofit.create(CourseService::class.java)
    }

    @Provides
    @Singleton
    fun provideCourseRepository(
        courseService: CourseService,
        courseDao: CourseDao
    ): CourseRepository { // Retorna la INTERFAZ (Contrato)
        return CourseRepositoryImpl(courseService, courseDao) // Retorna la IMPLEMENTACIÓN (Data)
    }

    @Provides
    @Singleton
    fun provideEnrollmentService(retrofit: Retrofit): EnrollmentService {
        return retrofit.create(EnrollmentService::class.java)
    }

    @Provides
    @Singleton
    fun provideEnrollmentRepository(
        enrollmentService: EnrollmentService,
    ): EnrollmentRepository { // Retorna la INTERFAZ (Contrato)
        return EnrollmentRepositoryImpl(enrollmentService) // Retorna la IMPLEMENTACIÓN (Data)
    }

    @Provides
    @Singleton
    fun provideQuizTemplateService(retrofit: Retrofit): QuizTemplateService {
        return retrofit.create(QuizTemplateService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuizTemplateRepository(
        quizTemplateService: QuizTemplateService,
    ): QuizTemplateRepository { // Retorna la INTERFAZ (Contrato)
        return QuizTemplateRepositoryImpl(quizTemplateService) // Retorna la IMPLEMENTACIÓN (Data)
    }

    @Provides
    @Singleton
    fun provideQuizQuestionService(retrofit: Retrofit): QuizQuestionService {
        return retrofit.create(QuizQuestionService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuizQuestionRepository(
        quizQuestionService: QuizQuestionService,
    ): QuizQuestionRepository { // Retorna la INTERFAZ (Contrato)
        return QuizQuestionRepositoryImpl(quizQuestionService) // Retorna la IMPLEMENTACIÓN (Data)
    }

    @Provides
    @Singleton
    fun provideScheduledQuizService(retrofit: Retrofit): ScheduledQuizService {
        return retrofit.create(ScheduledQuizService::class.java)
    }

    @Provides
    @Singleton
    fun provideScheduledQuizRepository(
        scheduledQuizService: ScheduledQuizService,
    ): ScheduledQuizRepository { // Retorna la INTERFAZ (Contrato)
        return ScheduledQuizRepositoryImpl(scheduledQuizService) // Retorna la IMPLEMENTACIÓN (Data)
    }

    @Provides
    @Singleton
    fun provideStudentQuizAttemptService(retrofit: Retrofit): StudentQuizAttemptService {
        return retrofit.create(StudentQuizAttemptService::class.java)
    }

    @Provides
    @Singleton
    fun provideStudentQuizAttemptRepository(
        studentQuizAttemptService: StudentQuizAttemptService,
    ): StudentQuizAttemptRepository { // Retorna la INTERFAZ (Contrato)
        return StudentQuizAttemptRepositoryImpl(studentQuizAttemptService) // Retorna la IMPLEMENTACIÓN (Data)
    }

    @Provides
    @Singleton
    fun provideSubjectConfigService(retrofit: Retrofit): SubjectConfigService {
        return retrofit.create(SubjectConfigService::class.java)
    }

    @Provides
    @Singleton
    fun provideSubjectConfigRepository(
        subjectConfigService: SubjectConfigService,
    ): SubjectConfigRepository { // Retorna la INTERFAZ (Contrato)
        return SubjectConfigRepositoryImpl(subjectConfigService) // Retorna la IMPLEMENTACIÓN (Data)
    }

    @Provides
    @Singleton
    fun provideCloudinaryService(): CloudinaryService {
        return CloudinaryService() // Como no tiene dependencias en el constructor, se instancia así
    }

    @Provides
    @Singleton
    fun provideCloudinaryRepository(
        cloudinaryService: CloudinaryService
    ): CloudinaryRepository { // Retorna la INTERFAZ
        return CloudinaryRepositoryImpl(cloudinaryService) // Retorna la IMPLEMENTACIÓN
    }

}