package com.jaco.cc3d.domain.session

object LanguageConfig {
    // Por defecto español, pero esto se actualizará desde la UI
    @Volatile // Asegura que los cambios sean visibles inmediatamente entre hilos
    var currentCode: String = "es"
}