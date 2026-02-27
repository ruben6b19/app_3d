package com.jaco.cc3d.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jaco.cc3d.data.local.entities.EnrollmentEntity

@Dao
interface EnrollmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(enrollments: List<EnrollmentEntity>)

    @Query("SELECT * FROM enrollments WHERE studentId = :studentId")
    suspend fun getEnrollmentsByStudent(studentId: String): List<EnrollmentEntity>

    @Query("DELETE FROM enrollments WHERE studentId = :studentId")
    suspend fun clearByStudent(studentId: String)

    @Query("DELETE FROM enrollments") // 👈 Esta es la magia de SQL
    suspend fun clearAll()
}