package com.jaco.cc3d.data.local.bible

// Archivo: SqliteBibleDao.kt

import com.jaco.cc3d.data.local.entities.BibleEntity
import com.jaco.cc3d.di.BibleFileManager
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.bookMap
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.normalizeBookName
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.parseCitation
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class SqliteBibleDao(private val fileManager: BibleFileManager) : BibleDao {

    // Un caché para las conexiones abiertas (para evitar abrir/cerrar repetidamente)
    private val dbConnections = mutableMapOf<String, Connection>()
    // --- NUEVO: Mapa para convertir nombres de libros a IDs numéricos ---
    // Asume un orden estándar de los libros de la Biblia.
    private val bookIdMap: Map<String, Int>

    init {
        // 1. Definimos el orden canónico de los libros que coincide con los IDs de la base de datos.
        val fullBookOrder = listOf(
            "Gen", "Ex", "Lev", "Num", "Deut", "Jos", "Jue", "Rut", "1 Sam", "2 Sam", "1 Rey", "2 Rey",
            "1 Cro", "2 Cro", "Esd", "Neh", "Est", "Job", "Sal", "Prov", "Ecl", "Cant", "Is", "Jer",
            "Lam", "Ez", "Dan", "Os", "Jl", "Am", "Abd", "Jon", "Miq", "Nah", "Hab", "Sof", "Hag", "Zac", "Mal",
            "Mt", "Mc", "Lc", "Jn", "Hch", "Rom", "1 Cor", "2 Cor", "Gál", "Ef", "Fil", "Col",
            "1 Tes", "2 Tes", "1 Tim", "2 Tim", "Tit", "Flm", "Heb", "Stg", "1 Pe", "2 Pe",
            "1 Jn", "2 Jn", "3 Jn", "Jud", "Ap"
        )

        // 2. Creamos el mapa directamente a partir de esa lista.
        //    'withIndex()' nos da el índice (0, 1, 2...)
        //    'associate' crea un mapa donde la llave es la abreviatura y el valor es el índice + 1.
        bookIdMap = fullBookOrder.withIndex().associate { (index, bookAbbr) ->
            bookAbbr to index + 1 // "Gen" to 1, "Ex" to 2, etc.
        }
    }

    // Función auxiliar para establecer y manejar la conexión a SQLite
    private fun connect(): Connection {
        // La URL de conexión para SQLite
        //val url = "jdbc:sqlite:$bblxFilePath"
        val url = "jdbc:sqlite:"
        //println("path: ${bblxFilePath}")
        return DriverManager.getConnection(url)
    }

    private fun getDbConnection(bibleId: String): Connection? {
        // 1. Revisa el caché
        if (dbConnections.containsKey(bibleId)) {
            return dbConnections[bibleId]
        }

        // 2. Si no está en caché, obtén la ruta
        val path = fileManager.getFilePath(bibleId)

        // Verifica si el archivo existe en esa ruta
        if (path == null || !File(path).exists()) {
            // Es importante que el archivo .bblx (SQLite DB) ya esté en esa ubicación
            return null
        }

        // 3. Usa DriverManager para abrir la conexión real a SQLite
        try {
            // El formato de URL para SQLite es "jdbc:sqlite:<ruta_al_archivo>"
            val connection = DriverManager.getConnection("jdbc:sqlite:$path")

            // 4. Cachea y devuelve
            dbConnections[bibleId] = connection
            return connection

        } catch (e: Exception) {
            // Manejo de error de conexión (ej. el archivo está corrupto o no es una DB válida)
            e.printStackTrace()
            return null
        }
    }



    /**
     * Función auxiliar para limpiar sufijos de los versículos (ej: "4a" -> 4)
     */
    private fun sanitizeVerse(verseValue: Any?): Int {
        if (verseValue == null) return 0
        return when (verseValue) {
            is Int -> verseValue
            is String -> {
                // Se queda solo con los dígitos (ej: "4a" se convierte en "4")
                verseValue.filter { it.isDigit() }.toIntOrNull() ?: 0
            }
            else -> 0
        }
    }

    /**
     * Obtiene una lista de versículos de la base de datos según una cita bíblica.
     *
     * @param citation La cita en formato de texto (ej: "Juan 3:16-18; 14:6").
     * @return Una lista de entidades `BibleEntity` correspondientes a la cita.
     */
    override suspend fun getVerses(bibleId: String, citation: String): List<BibleEntity> {
        val allBookNames: List<String> = (bookMap.keys + bookMap.values).toList()
        // Asumimos que parseCitation devuelve un ParsedCitation(book="Génesis", queries=[])
        // si la entrada es solo "Génesis".
        val parsedCitation = parseCitation(citation, allBookNames) ?: return emptyList()

        val normalizedAbbr = normalizeBookName(parsedCitation.book) ?: return emptyList()
        val bookId = bookIdMap[normalizedAbbr] ?: return emptyList()

        // --- Construcción de la consulta SQL dinámica ---
        val whereClauses = mutableListOf<String>()
        // Lista principal de parámetros para la consulta final
        val queryParams = mutableListOf<Any>()

        parsedCitation.queries.forEach { chapterQuery ->
            val chapter = chapterQuery.chapter

            if (chapterQuery.verses.isEmpty()) {
                // Caso 1: Se pide un capítulo completo (ej: "Génesis 1")
                whereClauses.add("(Chapter = ?)")
                queryParams.add(chapter)
            } else {
                // Caso 2: Se piden versículos o rangos específicos (ej: "Génesis 1:1-5, 8")
                val verseConditionsSql = mutableListOf<String>()
                val verseParams = mutableListOf<Any>()

                chapterQuery.verses.forEach { verseCondition ->
                    val startV = sanitizeVerse(verseCondition.startVerse)
                    val endV = if (verseCondition.endVerse != null) sanitizeVerse(verseCondition.endVerse) else null

                    if (endV != null) {
                        // Es un rango (ej: 3a-5b -> buscará BETWEEN 3 AND 5)
                        verseConditionsSql.add("(Verse BETWEEN ? AND ?)")
                        verseParams.add(startV)
                        verseParams.add(endV)
                    } else {
                        // Es un solo versículo (ej: 4a -> buscará Verse = 4)
                        verseConditionsSql.add("(Verse = ?)")
                        verseParams.add(startV)
                    }
                }

                whereClauses.add("(Chapter = ? AND (${verseConditionsSql.joinToString(" OR ")}))")
                queryParams.add(chapter) // 1. Añadir el capítulo
                queryParams.addAll(verseParams)  // 2. Añadir los versículos
            }
        }

        // --- ⭐ MODIFICACIÓN CLAVE PARA ACEPTAR LIBROS ENTEROS ---

        val sql: String
        val finalParams: List<Any>

        if (whereClauses.isEmpty()) {
            // Caso 3: No hay capítulos/versículos específicos (ej: "Génesis").
            // El bucle anterior no se ejecutó, pero tenemos un bookId válido.
            // Pedimos el libro entero.
            sql = "SELECT * FROM Bible WHERE Book = ? ORDER BY Chapter, Verse"
            finalParams = listOf(bookId)
        } else {
            // Casos 1 y 2: Se pidieron capítulos o versículos específicos.
            val finalWhere = whereClauses.joinToString(" OR ")
            sql = "SELECT * FROM Bible WHERE Book = ? AND ($finalWhere) ORDER BY Book, Chapter, Verse"
            // El ID del libro va primero, seguido de todos los demás parámetros
            finalParams = listOf(bookId) + queryParams
        }

        return executeQuery(bibleId,sql, finalParams)
    }

    // Archivo: SqliteBibleDao.kt

    private fun executeQuery(bibleId: String, sql: String, params: List<Any>): List<BibleEntity> {

        val verses = mutableListOf<BibleEntity>()
        // 1. Obtén la conexión (sin 'try-catch' aquí, getDbConnection ya maneja la creación)
        val conn = getDbConnection(bibleId) ?: return emptyList()

        // 2. Usa 'try-catch' solo para la operación de la consulta
        try {
            // 3. NO uses "conn.use { ... }" aquí
            conn.prepareStatement(sql).use { statement -> // Esto está bien
                // Asigna los parámetros de forma segura
                params.forEachIndexed { index, param ->
                    statement.setObject(index + 1, param)
                }

                statement.executeQuery().use { rs -> // Esto está bien
                    while (rs.next()) {
                        verses.add(
                            BibleEntity(
                                book = rs.getString("Book"),
                                chapter = rs.getString("Chapter"),
                                verse = rs.getString("Verse"),
                                scripture = rs.getString("Scripture")
                            )
                        )
                    }
                }
            }
        } catch (e: SQLException) {
            println("ERROR en el DAO al obtener versículos: ${e.message}")
            // Si la conexión está rota (ej. "database closed"),
            // podrías considerar eliminarla del caché aquí:
            // dbConnections.remove(bibleId)
        }
        return verses
    }
    private fun executeQuery2(bibleId: String, sql: String, params: List<Any>): List<BibleEntity> {

        val verses = mutableListOf<BibleEntity>()
        try {
            val connection = getDbConnection(bibleId) ?: return emptyList()
            connection.use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    // Asigna los parámetros de forma segura para evitar inyección SQL
                    params.forEachIndexed { index, param ->
                        statement.setObject(index + 1, param)
                    }

                    statement.executeQuery().use { rs ->
                        while (rs.next()) {
                            verses.add(
                                BibleEntity(
                                    book = rs.getString("Book"),
                                    chapter = rs.getString("Chapter"),
                                    verse = rs.getString("Verse"),
                                    scripture = rs.getString("Scripture")
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            println("ERROR en el DAO al obtener versículos: ${e.message}")
        }
        return verses
    }


    override suspend fun getVerse(book: Int, chapter: Int): List<BibleEntity> {
        val notes = mutableListOf<BibleEntity>()
        val sql = "SELECT * FROM Bible where Book = ${book} and Chapter = ${chapter} "
        println("A1")
        try {
            connect().use { conn -> // 'use' garantiza que la conexión se cierre automáticamente
                conn.createStatement().use { statement ->
                    statement.executeQuery(sql).use { rs ->
                        println("A2")
                        while (rs.next()) {
                            // Mapeo de columnas SQLite a nuestro modelo Kotlin
                            notes.add(
                                BibleEntity(
                                    // Z_PK es la Primary Key de SQLite
                                    book = rs.getString("Book"),
                                    chapter = rs.getString("Chapter"),
                                    verse = rs.getString("Verse"),
                                    scripture = rs.getString("Scripture")
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            println("ERROR en el DAO al obtener notas del BBLX: ${e.message}")
            // En una aplicación real, se lanzaría una excepción más específica o se registraría el error.
        }
        println(notes)
        return notes
    }
}