package com.jaco.cc3d.data.local.bible

import com.jaco.cc3d.data.local.entities.BibleEntity

// Archivo: BibleDao.kt
interface BibleDao {

    suspend fun getVerse(book: Int, chapter: Int): List<BibleEntity>

    suspend fun getVerses(bibleId: String, citation: String): List<BibleEntity>

    // fun getNoteById(id: String): BibleNote?
    // fun searchNotes(query: String): List<BibleNote>

}