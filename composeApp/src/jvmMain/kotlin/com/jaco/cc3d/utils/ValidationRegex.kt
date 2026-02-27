package com.jaco.cc3d.utils

import kotlin.text.Regex

/**
 * Clase estática que contiene todas las expresiones regulares (Regex) utilizadas para la validación
 * de datos en la aplicación, siguiendo la convención de JS/TS (patrones anclados ^$).
 */
object ValidationRegex {
    // Patrones Anclados: Se aseguran de que la cadena coincida de principio a fin.

    // General (Texto y Números)
    val VALIDATE_CHAT = Regex("""^[/:#()*'"-.,;?¿!¡a-zA-ZñÑáéíóúÁÉÍÓÚ0-9 ]{1,100}$""")
    val VALIDATE_TEXT = Regex("""^[/:#()*'"-.,;?¿!¡a-zA-ZñÑáéíóúÁÉÍÓÚ0-9 ]{0,50}$""")
    val VALIDATE_DESCRIPTION = Regex("""^[/:#()*'"-.,;?¿!¡a-zA-ZñÑáéíóúÁÉÍÓÚ0-9 ]{0,100}$""")
    val VALIDATE_FOUR_DIGITS = Regex("""^[0-9]{4}$""")
    val VALIDATE_SEARCH = Regex("""^[/:#()*'"-.,;?¿!¡a-zA-ZñÑáéíóúÁÉÍÓÚ0-9 ]{1,30}$""")
    val VALIDATE_NAME_ADDRESS = Regex("""^[/:#()*'"-.,;?¿!¡a-zA-ZñÑáéíóúÁÉÍÓÚ0-9 ]{0,50}$""")
    val VALIDATE_DESCRIPTION_ADDRESS = Regex("""^[/:#()*'"-.,;?¿!¡a-zA-ZñÑáéíóúÁÉÍÓÚ0-9 ]{0,150}$""")
    val VALIDATE_IMAGE = Regex("""^[a-zA-Z0-9]{0,20}$""")
    val VALIDATE_CLOUD_INARY_ID = Regex("""^[a-z0-9]{20,22}$""")
    val VALIDATE_BAR_CODE = Regex("""^[a-zA-Z0-9]{5,20}$""")
    val VALIDATE_SHORT_NAME = Regex("""^[/:#()*'"-.,;?¿!¡A-ZÑÁÉÍÓÚ0-9 ]{2}$""")
    val VALIDATE_PLAQUE = Regex("""^[0-9]{3,4}[A-Z]{3}$""")
    val VALIDATE_CI = Regex("""^[/()'"-.,;?¿!¡a-zA-ZñÑáéíóúÁÉÍÓÚ0-9 ]{7,10}$""")


    // Fechas y Tiempos
    val VALIDATE_CREATED = Regex("""^[0-9]{10}$""")
    val VALIDATE_UPDATED = Regex("""^([0]{1})|([0-9]{10})$""")
    val VALIDATE_SCHEDULE = Regex("""^([01]?[0-9]|2[0-3]):[0-5][0-9](,([01]?[0-9]|2[0-3]):[0-5][0-9])?$$""")
    val VALIDATE_YEAR = Regex("""^[/0-9 ]{4}$""")

    // Números y Precios
    val VALIDATE_NUMBER = Regex("""^[/:.,-;0-9 ]{1,50}$""")
    val VALIDATE_NUMBER_OF_ONE_DIGIT = Regex("""^[0-9]{1}$""")
    val VALIDATE_DECIMAL_RADIUS = Regex("""^([0-9]{1,2})([.][0-9]{2,9})$""")
    val VALIDATE_DECIMAL = Regex("""^([0-9]{1,3})([.][0-9]{2})?$$""")
    val VALIDATE_DECIMAL_FOR_PRICE = Regex("""^([0-9]{1,5})([.][0-9]{1,12})?$$""")

    // Contacto
    val VALIDATE_EMAIL = Regex("""^[a-zA-Z0-9._%+-]{1,64}@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$$""")
    val VALIDATE_WEBSITE = Regex("""^((https?|ftp|smtp):\/\/)?(www.)?[a-z0-9]+(\.[a-z]{2,}){1,3}(#?\/?[a-zA-Z0-9#]+)*\/?(\?[a-zA-Z0-9-_]+=[a-zA-Z0-9-%]+&?)?$$""")
    val VALIDATE_MOBILE = Regex("""^[0-9]{8,10}(,[0-9]{8,10})?$$""")
    val VALIDATE_PHONE = Regex("""^[0-9]{5,10}(,[0-9]{5,10})?$$""")
    val VALIDATE_PASS = Regex("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$""")
    val VALIDATE_NAME = Regex("""^[-'a-zA-ZñÑáéíóúÁÉÍÓÚ ]{2,50}$""")

    // Coordenadas (Geolocalización)
    // El punto decimal (.) necesita doble escape o el uso de triple comillas para que funcione correctamente.
    val VALIDATE_LATITUDE = Regex("""^([-]?[0-9]{1,3})(\.[0-9]{2,15})$""")
    val VALIDATE_LONGITUDE = Regex("""^([-]?[0-9]{1,3})(\.[0-9]{2,15})$""")

    // Otros
    const val MAX_TIMESTAMP = 2147483647
    const val MAX_STATUS = 2
    const val MIN_STATUS = 0
}