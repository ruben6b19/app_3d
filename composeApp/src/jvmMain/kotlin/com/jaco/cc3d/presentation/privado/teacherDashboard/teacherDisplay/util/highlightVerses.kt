package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util

import java.util.regex.Pattern

// Mapa de referencia para nombres y abreviaturas de los libros
val bookMap = mapOf(
    // --- ANTIGUO TESTAMENTO: Pentateuco ---
    "Génesis" to "Gen",
    "Éxodo" to "Ex",
    "Levítico" to "Lev",
    "Números" to "Num",
    "Deuteronomio" to "Deut",

    // --- ANTIGUO TESTAMENTO: Históricos ---
    "Josué" to "Jos",
    "Jueces" to "Jue",
    "Rut" to "Rut",
    "1 Samuel" to "1 Sam",
    "2 Samuel" to "2 Sam",
    "1 Reyes" to "1 Rey",
    "2 Reyes" to "2 Rey",
    "1 Crónicas" to "1 Cro",
    "2 Crónicas" to "2 Cro",
    "Esdras" to "Esd",
    "Nehemías" to "Neh",
    "Ester" to "Est",

    // --- ANTIGUO TESTAMENTO: Poéticos y Sapienciales ---
    "Job" to "Job",
    "Salmos" to "Sal", // Ya incluido
    "Proverbios" to "Prov",
    "Eclesiastés" to "Ecl",
    "Cantares" to "Cant", // O "Cantar de los Cantares"

    // --- ANTIGUO TESTAMENTO: Profetas Mayores ---
    "Isaías" to "Is",
    "Jeremías" to "Jer",
    "Lamentaciones" to "Lam",
    "Ezequiel" to "Ez",
    "Daniel" to "Dan",

    // --- ANTIGUO TESTAMENTO: Profetas Menores ---
    "Oseas" to "Os",
    "Joel" to "Jl",
    "Amós" to "Am",
    "Abdías" to "Abd",
    "Jonás" to "Jon",
    "Miqueas" to "Miq",
    "Nahúm" to "Nah",
    "Habacuc" to "Hab",
    "Sofonías" to "Sof",
    "Hageo" to "Hag",
    "Zacarías" to "Zac",
    "Malaquías" to "Mal",

    // ------------------------------------------------------------------
    // --- NUEVO TESTAMENTO: Evangelios ---
    "Mateo" to "Mt",
    "Marcos" to "Mc",
    "Lucas" to "Lc",
    "Juan" to "Jn", // Ya incluido

    // --- NUEVO TESTAMENTO: Histórico ---
    "Hechos" to "Hch",

    // --- NUEVO TESTAMENTO: Epístolas de Pablo ---
    "Romanos" to "Rom",
    "1 Corintios" to "1 Cor",
    "2 Corintios" to "2 Cor",
    "Gálatas" to "Gál",
    "Efesios" to "Ef",
    "Filipenses" to "Fil",
    "Colosenses" to "Col",
    "1 Tesalonicenses" to "1 Tes",
    "2 Tesalonicenses" to "2 Tes",
    "1 Timoteo" to "1 Tim",
    "2 Timoteo" to "2 Tim",
    "Tito" to "Tit",
    "Filemón" to "Flm",

    // --- NUEVO TESTAMENTO: Epístolas Generales ---
    "Hebreos" to "Heb",
    "Santiago" to "Stg",
    "1 Pedro" to "1 Pe",
    "2 Pedro" to "2 Pe",
    "1 Juan" to "1 Jn",
    "2 Juan" to "2 Jn",
    "3 Juan" to "3 Jn",
    "Judas" to "Jud",

    // --- NUEVO TESTAMENTO: Profético ---
    "Apocalipsis" to "Ap"
)



/**
 * Normaliza el nombre del libro.
 * Maneja tanto el nombre completo (incluyendo el número) como las abreviaturas.
 *
 * @param rawBookName El nombre o abreviatura extraída del texto (ej: "2 Corintios" o "2 Cor").
 * @return La abreviatura estandarizada del libro (ej: "2 Cor").
 */
fun normalizeBookName(rawBookName: String): String? {
    // FUNCIÓN DE LIMPIEZA CLAVE: Elimina espacios y convierte a minúsculas
    fun clean(text: String) = text.replace("\\s|-".toRegex(), "").lowercase()

    val cleanInput = clean(rawBookName)

    // 1. Búsqueda por Nombre Completo (Clave del mapa)
    val byFullName = bookMap.entries.firstOrNull { (fullName, _) ->
        // Compara el nombre completo del mapa (limpio) con la entrada (limpia)
        clean(fullName) == cleanInput
    }?.value

    if (byFullName != null) {
        return byFullName
    }

    // 2. Búsqueda por Abreviatura (Valor del mapa)
    val byAbbreviation = bookMap.entries.firstOrNull { (_, abbr) ->
        // Compara la abreviatura del mapa (limpia) con la entrada (limpia)
        clean(abbr) == cleanInput
    }?.value

    return byAbbreviation
}


fun highlightVerses(text: String): String {
    val allBookNames = bookMap.keys + bookMap.values
    val sortedBookNames = allBookNames.sortedByDescending { it.length }

    // Usamos [ \t]* para que los espacios internos del libro sean solo HORIZONTALES
    val bookPattern = sortedBookNames.joinToString("|") {
        Pattern.quote(it).replace("\\ ", "[ \t]*")
    }

    // EXPLICACIÓN DEL PATRÓN MEJORADO:
    // (?iU)        -> i: Case-insensitive, U: UNICODE (hace que 'á' sea parte de la palabra y no un límite)
    // \b           -> Límite de palabra inicial (evita n_os_)
    // (?<book>...) -> El nombre del libro
    // (?:          -> Grupo para la parte numérica (capítulo y versículo)
    //    [ \t]+    -> OBLIGA a que haya al menos un espacio horizontal (evita saltos de línea \n)
    //    \d+[a-z]? -> El número de capítulo
    //    (?:[ \t,;:-]+\d+[a-z]?)* -> Versículos adicionales
    // )?           -> Lo hacemos opcional (?) PERO con validación extra abajo
    // \b           -> Límite de palabra final
    // (?![ \t]*\d+\.) -> Lookahead negativo: No capturar si lo que sigue es un punto de lista (ej: "2.")

    //val regexPattern = "(?iU)\\b(?<book>${bookPattern})(?:[ \t]+\\d+[a-z]?(?:[ \t,;:-]+\\d+[a-z]?)*)?\\b(?![ \t]*\\d+\\.)"
    //val regexPattern = "(?iU)\\b(?<book>${bookPattern})(?:[ \t]+\\d+[a-z]?(?:[ \t,;:-]+\\d+[a-z]?)*)?\\b(?![ \t]*\\d+\\.)"
    val regexPattern = "(?iU)\\b(?<book>${bookPattern})(?:[ \t]+\\d+[a-z]?(?:[ \t,;:-]+(?!\\b(?:${bookPattern})\\b)\\d+[a-z]?)*)?\\b(?![ \t]*\\d+\\.)"

    val regex = Pattern.compile(regexPattern)
    val matcher = regex.matcher(text)

    val result = StringBuilder()
    var lastEnd = 0

    while (matcher.find()) {
        val fullRef = matcher.group().trim()
        val rawBookName = matcher.group("book")

        // --- VALIDACIÓN DE SEGURIDAD EXTRA ---
        // Si el match es solo el libro (sin números) y es una abreviatura corta (<= 3 letras),
        // lo ignoramos para evitar "Os", "Est", "Jn" perdidos en el texto.
        // Solo permitimos libros solos si son nombres largos (Génesis, Mateo, etc.)
        val hasNumbers = fullRef.length > rawBookName.length
        if (!hasNumbers && rawBookName.length <= 3) {
            continue
        }

        // Adjuntar texto previo
        result.append(text.substring(lastEnd, matcher.start()))

        // Adjuntar cita con marcadores
        result.append(HIGHLIGHT_MARKER)
        result.append(fullRef)
        result.append(HIGHLIGHT_MARKER)

        lastEnd = matcher.end()
    }

    result.append(text.substring(lastEnd))
    return result.toString()
}

/**
 * Retorna una lista de las citas bíblicas encontradas en el texto en orden de aparición.
 */
fun extractVerseCitations(text: String): List<String> {
    val bookPattern = bookMap.keys.joinToString("|") { Pattern.quote(it) } + "|" +
            bookMap.values.joinToString("|") { Pattern.quote(it) }

    val regexPattern = "(?iU)\\b(?<book>${bookPattern})(?:[ \\t]+\\d+[a-z]?(?:[ \\t,;:-]+(?!\\b(?:${bookPattern})\\b)\\d+[a-z]?)*)?\\b"
    val matcher = Pattern.compile(regexPattern).matcher(text)

    val foundCitations = mutableListOf<String>()
    while (matcher.find()) {
        foundCitations.add(matcher.group().trim())
    }
    return foundCitations.distinct() // Solo únicas para no repetir teclas por la misma cita
}