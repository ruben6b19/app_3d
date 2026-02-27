package com.jaco.cc3d.di

import com.jaco.cc3d.data.network.auth.AuthService
import com.jaco.cc3d.data.repositories.auth.AuthRepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
object AuthModule {

    // 🚨 IMPORTANTE: Reemplaza esta con tu clave real de Firebase Web API.
    // En una aplicación de producción, esta clave debería cargarse desde un archivo de configuración
    // o variable de entorno, NUNCA en texto plano.
    private const val FIREBASE_API_KEY = "AIzaSyCJIJe3k6I26jeks3nocr1Wo8OC2ud9T2E"

    private const val BASE_URL_FIREBASE_AUTH = "https://identitytoolkit.googleapis.com/"
    //private const val BASE_URL_FIREBASE_AUTH = "https://securetoken.googleapis.com/"
    private const val AUTH_RETROFIT = "AuthRetrofit"

    //@Provides
    //@Singleton
    //fun provideApiKey(): String = FIREBASE_API_KEY
    @Provides
    @Singleton
    @Named("ApiKey") // 🎯 Calificador para la clave API
    fun provideApiKey(): String = FIREBASE_API_KEY

    /*@Provides
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
    }*/

    @Provides
    @Singleton
    @Named(AUTH_RETROFIT)
    fun provideRetrofit(contentType: MediaType, json: Json): Retrofit {
        // Configuramos Retrofit, inyectando las dependencias que proveemos arriba
        return Retrofit.Builder()
            .baseUrl(BASE_URL_FIREBASE_AUTH)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }


    @Provides
    @Singleton
    fun provideAuthService(@Named(AUTH_RETROFIT) retrofit: Retrofit): AuthService {
        // 🛑 CAMBIO CLAVE: Solicitamos la instancia calificada para Firebase
        return retrofit.create(AuthService::class.java)
    }

    /**
     * Provee el repositorio.
     * Dagger inyecta el AuthService y el String (API Key) automáticamente.
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        @Named("ApiKey") apiKey: String // 🎯 Usamos el calificador para inyectar la clave correcta
    ): AuthRepositoryImpl {
        return AuthRepositoryImpl(authService, apiKey)
    }
}
