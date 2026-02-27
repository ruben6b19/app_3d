package com.jaco.cc3d.domain.models


/**
 * Modelo de Dominio: Representa un versículo de la Biblia en la capa de lógica de negocio.
 * Es independiente de la fuente de datos (SQLite/Entity).
 */
data class BibleVerse(
    val book: String,
    val chapter: String,
    val verse: String?,
    val text: String // Usamos 'text' en el dominio, en lugar de 'scripture' de la Entity
)