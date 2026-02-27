package com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.util

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

/**
 * Abre el selector de archivos nativo del sistema operativo.
 */
fun openMarkdownFilePicker(onFileSelected: (File) -> Unit) {
    val dialog = FileDialog(null as Frame?, "Seleccionar archivo Markdown (.md)", FileDialog.LOAD).apply {
        file = "*.md" // Filtro para solo archivos markdown
        setFilenameFilter { _, name -> name.endsWith(".md") }
        isVisible = true
    }

    if (dialog.file != null) {
        val file = File(dialog.directory, dialog.file)
        onFileSelected(file)
    }
}