// com/jaco/cc3d/utils/DateUtils.kt

package com.jaco.cc3d.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * Convierte una cadena de fecha en formato ISO 8601 (e.g., 2012-11-11T00:00:00.000+00:00)
 * al formato DD/MM/YYYY legible.
 */
fun formatIsoDateToDdMmYyyy(isoDate: String): String {
    return try {
        val instant = Instant.parse(isoDate)
        // Nos aseguramos de interpretar la fecha como UTC antes de formatear.
        val localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate()

        // 💡 CORRECCIÓN: Especificar Locale.US o Locale.ROOT para forzar la interpretación literal del patrón,
        // o Locale("es", "ES") para un contexto español. Usaremos Locale.ROOT para forzar el patrón.
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT)

        localDate.format(formatter)
    } catch (e: Exception) {
        // En caso de error de parseo (fecha inválida), devolvemos vacío.
        ""
    }
}

fun convertDdMmYyyyToIso(dateString: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT) // 💡 MEJORA CONSISTENTE
    val outputFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    return try {
        val date = LocalDate.parse(dateString, inputFormatter)
        date.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        throw IllegalArgumentException("Formato de fecha inválido: $dateString. Se esperaba DD/MM/AAAA.")
    }
}