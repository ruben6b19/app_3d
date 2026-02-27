package com.jaco.cc3d.core.config


object CloudinaryConfig {
    // 1. Datos base
    const val CLOUD_NAME = "dxlnemk98"
    private const val BASE_URL = "https://res.cloudinary.com/$CLOUD_NAME"

    // 2. URLs por tipo de recurso
    const val RAW_BASE_URL = "$BASE_URL/raw/upload"
    const val IMAGE_BASE_URL = "$BASE_URL/image/upload"

    // 3. Presets (Rutas de carpetas)
    object Presets {
        const val MD = "cc3d/md"
        const val PROFILE_PICTURES = "cc3d/profiles"
        const val SUBJECT_COVERS = "cc3d/covers"
    }

    fun getMdUrl(id: String) = buildUrl(RAW_BASE_URL, Presets.MD, id)
    fun getProfileUrl(id: String) = buildUrl(IMAGE_BASE_URL, Presets.PROFILE_PICTURES, id)

    fun buildUrl(baseUrl: String, preset: String, contentId: String): String {
        // Limpiamos el contentId por si acaso ya incluye el preset
        //val cleanId = contentId.removePrefix("$preset/")
        return "$baseUrl/$preset/$contentId"
    }
}