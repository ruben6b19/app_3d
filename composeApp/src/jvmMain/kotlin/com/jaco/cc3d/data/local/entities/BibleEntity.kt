package com.jaco.cc3d.data.local.entities


data class BibleEntity(
    val book: String,
    val chapter: String,
    val verse: String?,
    val scripture: String // Usamos Long para el timestamp
)


//fun Quote.toDatabase() = QuoteEntity(quote = quote, author =  author)