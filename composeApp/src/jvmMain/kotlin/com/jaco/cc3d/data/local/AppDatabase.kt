package com.jaco.cc3d.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jaco.cc3d.data.local.dao.CourseDao
import com.jaco.cc3d.data.local.dao.EnrollmentDao
import com.jaco.cc3d.data.local.dao.UserDao
import com.jaco.cc3d.data.local.entities.CourseEntity
import com.jaco.cc3d.data.local.entities.EnrollmentEntity
import com.jaco.cc3d.data.local.entities.StudentEntity
import com.jaco.cc3d.data.local.entities.UserEntity

@Database(
    entities = [CourseEntity::class, UserEntity::class, EnrollmentEntity::class, StudentEntity::class], // Agrega aquí PresentationEntity cuando la crees
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun userDao(): UserDao
    abstract fun enrollmentDao(): EnrollmentDao
}