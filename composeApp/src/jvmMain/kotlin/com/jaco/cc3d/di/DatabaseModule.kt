package com.jaco.cc3d.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.jaco.cc3d.data.local.AppDatabase
import com.jaco.cc3d.data.local.dao.CourseDao
import com.jaco.cc3d.data.local.dao.EnrollmentDao
import com.jaco.cc3d.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase {
        // Carpeta persistente: C:\Users\Nombre\.cc3d_data\
        val dbFile = File(System.getProperty("user.home"), ".cc3d_data/cc3d_main.db")
        if (!dbFile.parentFile.exists()) dbFile.parentFile.mkdirs()

        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
        )
            .setDriver(BundledSQLiteDriver()) // Requerido para Desktop
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideCourseDao(database: AppDatabase): CourseDao {
        return database.courseDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideEnrollmentDao(database: AppDatabase): EnrollmentDao {
        return database.enrollmentDao()
    }
}