package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

/**
 * Abre un diálogo nativo para seleccionar un archivo .md y lee su contenido.
 * @return El contenido del archivo MD como String, o null si el usuario cancela o hay un error.
 */
fun pickMarkdownFile(): String? {
    // Usamos JFileChooser (más moderno y preferido en Swing/Desktop)
    val chooser = JFileChooser()

    // Configurar para mostrar solo archivos (no directorios)
    chooser.fileSelectionMode = JFileChooser.FILES_ONLY

    // Opcional: Establecer un filtro para archivos .md
    chooser.fileFilter = object : FileFilter() {
        override fun accept(f: File): Boolean {
            return f.isDirectory || f.name.lowercase().endsWith(".md")
        }

        override fun getDescription(): String {
            return "Archivos Markdown (*.md)"
        }
    }

    // Mostrar el diálogo de apertura
    val result = chooser.showOpenDialog(null) // null para usar la ventana padre por defecto

    if (result == JFileChooser.APPROVE_OPTION) {
        val selectedFile = chooser.selectedFile
        if (selectedFile != null) {
            return try {
                // Usamos la función de extensión de Kotlin para leer el texto de forma concisa.
                selectedFile.readText(Charsets.UTF_8)
            } catch (e: Exception) {
                println("Error al leer el archivo: ${e.message}")
                null
            }
        }
    }
    // Retorna null si el usuario cancela o no selecciona un archivo
    return null
}