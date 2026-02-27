package com.jaco.cc3d.data.mappers


import com.jaco.cc3d.data.local.entities.BibleEntity
import com.jaco.cc3d.domain.models.BibleVerse

/**
 * Función de extensión para mapear un BibleEntity (Data Layer)
 * a un BibleVerse (Domain Layer).
 */
fun BibleEntity.toDomainModel(): BibleVerse {
    return BibleVerse(
        book = this.book,
        chapter = this.chapter,
        verse = this.verse,
        text = this.scripture // Mapea 'scripture' de la Entity a 'text' del Domain Model
    )
}

/**
 * Mapea una lista de Entities a una lista de Domain Models.
 */
fun List<BibleEntity>.toDomainModelList(): List<BibleVerse> {
    return this.map { it.toDomainModel() }
}