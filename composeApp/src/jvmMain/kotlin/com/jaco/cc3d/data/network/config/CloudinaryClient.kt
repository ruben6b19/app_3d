package com.jaco.cc3d.data.network.config

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils

object CloudinaryClient {
    // 💡 Tip: En el futuro, podrías leer estos valores de un archivo .properties o variables de entorno
    private const val CLOUD_NAME = "dxlnemk98"
    private const val API_KEY = "822961184465388"
    private const val API_SECRET = "3PIG3c-aX8IC7BPaK_SFMVn0ycw"

    val instance: Cloudinary by lazy {
        Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET,
                "secure", true
            )
        )
    }
}