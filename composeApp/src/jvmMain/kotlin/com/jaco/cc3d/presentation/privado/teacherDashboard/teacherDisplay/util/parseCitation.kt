package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util

import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models.ChapterQuery
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models.ParsedCitation
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models.VerseCondition


//private val chapterOnlyRegex = "^(\\d+)$".toRegex()
private val standardRegex = Regex("""(\d+)\s*:\s*([\d,a-zA-Z\s-]+)""")
private val crossChapterRegex = Regex("""(\d+)\s*:\s*(\d+[a-zA-Z]?)\s*-\s*(\d+)\s*:\s*(\d+[a-zA-Z]?)""")
private val chapterOnlyRegex = Regex("""(\d+)""")

val singleChapterBooks = setOf("Abd", "Flm", "2 Jn", "3 Jn", "Jud")

/**
 * Analiza una cadena de cita bíblica y la convierte en un objeto estructurado.
 *
 * @param citation La cadena de texto (ej: "Mateo 5:3-12; 7:24-27" o "Lucas 1:26-2:7").
 * @param allBookNames Una lista de todos los nombres y abreviaturas de libros válidos.
 * @return Un objeto [ParsedCitation] o null si el formato es inválido.
 */
fun parseCitation(citation: String, allBookNames: List<String>): ParsedCitation? {
    val trimmedCitation = citation.trim()
    if (trimmedCitation.isEmpty()) return null

    // 1. Encontrar el nombre del libro
    val bookName = allBookNames
        .sortedByDescending { it.length }
        .firstOrNull { trimmedCitation.startsWith(it, ignoreCase = true) }
        ?: return null

    // Normalizamos para saber si es un libro de un solo capítulo
    val normalizedAbbr = normalizeBookName(bookName)
    val isSingleChapter = singleChapterBooks.contains(normalizedAbbr)

    val numbersPart = trimmedCitation.substring(bookName.length).trim()
    if (numbersPart.isEmpty()) {
        return ParsedCitation(bookName, emptyList())
    }

    val chapterQueries = mutableListOf<ChapterQuery>()
    // Separamos por ";" (Ej: "Mateo 5; 6" -> ["5", "6"])
    val chapterParts = numbersPart.split(';').map { it.trim() }.filter { it.isNotEmpty() }
    val END_OF_CHAPTER = "9999"

    for (part in chapterParts) {
        when {
            // CASO 1: Rango entre capítulos ("1:26-2:7") - Sin cambios
            crossChapterRegex.matches(part) -> {
                val match = crossChapterRegex.matchEntire(part)!!
                val (startChapStr, startVerse, endChapStr, endVerse) = match.destructured
                val startChap = startChapStr.toInt()
                val endChap = endChapStr.toInt()

                if (startChap > endChap) continue // <--- CORREGIDO: Usamos 'continue'

                if (startChap == endChap) {
                    chapterQueries.add(ChapterQuery(startChap, listOf(VerseCondition(startVerse, endVerse))))
                } else {
                    chapterQueries.add(ChapterQuery(startChap, listOf(VerseCondition(startVerse, END_OF_CHAPTER))))
                    for (chap in (startChap + 1) until endChap) {
                        chapterQueries.add(ChapterQuery(chap))
                    }
                    chapterQueries.add(ChapterQuery(endChap, listOf(VerseCondition("1", endVerse))))
                }
            }

            // CASO 2: Formato estándar "Capítulo:Versículos" ("1:4-5, 8")
            standardRegex.matches(part) -> {
                val match = standardRegex.matchEntire(part)!!
                val (chapterStr, versesStr) = match.destructured
                val chapter = chapterStr.toIntOrNull() ?: continue

                val verseConditions = mutableListOf<VerseCondition>()
                val verseSegments = versesStr.split(',').map { it.trim() }.filter { it.isNotEmpty() }

                for (segment in verseSegments) {
                    val rangeParts = segment.split('-').map { it.trim() }
                    val startVerse = rangeParts.getOrNull(0) ?: continue
                    val endVerse = rangeParts.getOrNull(1)
                    verseConditions.add(VerseCondition(startVerse, endVerse))
                }
                chapterQueries.add(ChapterQuery(chapter, verseConditions))
            }

            // CASO 3: Listas de números o números sueltos ("9, 10, 11" o "3")
            // Reemplazamos el "chapterOnlyRegex" por una lógica que maneja comas
            else -> {
                val segments = part.split(',').map { it.trim() }.filter { it.isNotEmpty() }
                for (segment in segments) {
                    val num = segment.toIntOrNull() ?: continue

                    if (isSingleChapter) {
                        // Si es "Judas 3" -> Libro Judas, Capítulo 1, Versículo 3
                        chapterQueries.add(ChapterQuery(1, listOf(VerseCondition(num.toString()))))
                    } else {
                        // Si es "Romanos 9, 10" -> Capítulos completos 9 y 10
                        chapterQueries.add(ChapterQuery(num))
                    }
                }
            }
        }
    }
    return ParsedCitation(bookName, chapterQueries)
}
