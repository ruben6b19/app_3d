package com.jaco.cc3d.domain.repositories.bible

import com.jaco.cc3d.domain.models.BibleVerse

interface BibleRepository {
    suspend fun getVerse(book: Int, chapter: Int): List<BibleVerse>
    suspend fun getVerses(bibleId: String, citation: String): List<BibleVerse>
}