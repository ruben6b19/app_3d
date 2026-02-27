package com.jaco.cc3d.data.repositories.bible

import com.jaco.cc3d.data.local.bible.BibleDao
import com.jaco.cc3d.data.mappers.toDomainModelList
import com.jaco.cc3d.domain.models.BibleVerse
import com.jaco.cc3d.domain.repositories.bible.BibleRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de la Biblia.
 * Dagger inyecta el BibleDao, lo que permite a esta clase acceder a la fuente de datos.
 */
@Singleton
class BibleRepositoryImpl @Inject constructor(
    private val bibleDao: BibleDao
) : BibleRepository {

    override suspend fun getVerse(book: Int, chapter: Int): List<BibleVerse> {
        // En un caso más complejo, aquí se manejaría el mapeo de Entity a Model de Dominio,
        // la lógica de caché o la elección entre fuente local/remota.

        // 1. Obtener datos de la fuente (DAO) -> Devuelve List<BibleEntity>
        val entities = bibleDao.getVerse(book, chapter)

        // 🔑 2. Mapear las Entities a Domain Models antes de devolver
        return entities.toDomainModelList()
    }

    override suspend fun getVerses(bibleId: String, citation: String): List<BibleVerse> {
        val entities = bibleDao.getVerses(bibleId, citation)
        return entities.toDomainModelList()
    }


}