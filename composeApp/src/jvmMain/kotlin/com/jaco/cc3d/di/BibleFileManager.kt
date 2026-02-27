package com.jaco.cc3d.di

import java.io.File


class BibleFileManager {
    private val homeDir = System.getProperty("user.home")
    private val biblesRoot = File(homeDir, "bibles/")

    init {
        // Asegura que la carpeta raíz de las biblias exista
        if (!biblesRoot.exists()) {
            biblesRoot.mkdirs()
        }
    }

    // Define las biblias que la app espera y sus nombres de archivo
    private val availableBibles = mapOf(
        "RV60" to "Biblia Reina Valera 1960.bblx",
        "NTV" to "NTV Nueva Traducción Viviente.bblx",
        "LBLA" to "Biblia de las Américas.bblx",
        "BAD" to "Biblia al Día.bblx",
        "SEP" to "Septuaginta con Strong LXX+.bblx",
        //Biblia al Día.bblx
        // Agrega más según necesites
    )

    fun getFilePath(bibleId: String): String? {
        val fileName = availableBibles[bibleId]
        return if (fileName != null) {
            File(biblesRoot, fileName).absolutePath
        } else {
            null // O lanza una excepción si la Biblia no es soportada
        }
    }

    fun getAvailableBibleIds(): Set<String> = availableBibles.keys
}