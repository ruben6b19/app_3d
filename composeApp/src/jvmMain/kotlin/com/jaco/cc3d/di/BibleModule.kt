package com.jaco.cc3d.di

import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.jaco.cc3d.data.local.bible.BibleDao
import com.jaco.cc3d.data.local.bible.SqliteBibleDao
import com.jaco.cc3d.domain.repositories.bible.BibleRepository
import com.jaco.cc3d.data.repositories.bible.BibleRepositoryImpl
import java.io.File
import javax.inject.Named

@Module
object BibleModule {

    /**
     * Provee el Singleton que maneja las rutas de todas las biblias.
     * Reemplaza provideBblxFilePath().
     */
    @Provides
    @Singleton
    fun provideBibleFileManager(): BibleFileManager {
        return BibleFileManager()
    }

    /**
     * Provee la ruta del archivo BBLX para el entorno JVM de escritorio.
     * Ya no usa @ApplicationContext.
     */
    @Provides
    @Singleton
    @Named("BibleFilePath") // 🎯 Calificador para la ruta de la biblia
    fun provideBblxFilePath(): String {
        // Lógica específica para obtener la ruta en un entorno Desktop
        val homeDir = System.getProperty("user.home")
        val appDataDir = File(homeDir, ".cc3d_appdata/bibles/")
        val bblxFile = File(appDataDir, "Biblia Reina Valera 1960.bblx")

        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }
        return bblxFile.absolutePath
    }

    /**
     * Provee la implementación de BibleDao como Singleton.
     * Dagger inyecta el String (ruta) definido arriba automáticamente.
     */
    @Provides
    @Singleton
    fun provideBibleDao(fileManager: BibleFileManager): BibleDao {
        return SqliteBibleDao(fileManager)
    }

    // 🔑 NUEVA FUNCIÓN: Dagger sabrá cómo proveer el BibleDao
    @Provides
    @Singleton
    fun provideBibleRepository(bibleDao: BibleDao): BibleRepository {
        // Devuelve la implementación del repositorio, pasándole el DAO
        return BibleRepositoryImpl(bibleDao)
    }
}