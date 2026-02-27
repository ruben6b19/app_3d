package com.jaco.cc3d.presentation.app.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



// =======================================================
// 1. ESQUEMA DE COLOR CLARO (LIGHT)
// =======================================================

private val LightColorScheme = lightColorScheme(
    // Colores principales
    primary = PrimaryPurple,
    secondary = SecondaryMagenta,
    tertiary = AccentColor,

    // Colores de fondo/superficie
    background = LightBackgroundSurfaceBase, // Usando variable global
    surface = LightBackgroundSurfaceBase,    // Usando variable global

    // Colores de texto/iconos SOBRE los colores principales
    onPrimary = LightOnPrimaryColor, // Usando variable
    onSecondary = LightOnSecondaryColor, // Usando variable
    onTertiary = LightOnTertiaryColor, // Usando variable

    // Colores de texto/iconos SOBRE el fondo/superficie
    onBackground = PrimaryTextColor, // Texto principal sobre fondo
    onSurface = PrimaryTextColor, // Texto principal sobre tarjetas/superficies
    onSurfaceVariant = SecondaryTextColor, // TEXTO SECUNDARIO

    // Color de contorno o para hints muy ligeros.
    outline = HintTextColor, // HINT TEXT COLOR
)

// =======================================================
// 2. ESQUEMA DE COLOR OSCURO (DARK)
// =======================================================

private val DarkColorScheme = darkColorScheme(
    // Colores de acento (botones, elementos activos)
    primary = LightPurple,     // Se mantiene el primario
    secondary = LightMagenta,     // Usamos el tono más claro para mejor visibilidad
    tertiary = AccentColor,    // ACCENT COLOR (El color de acento se mantiene)

    // Colores de fondo para la pantalla y componentes
    background = DarkBackground,
    surface = DarkBackground,

    // Colores de texto/iconos SOBRE los colores principales
    onPrimary = DarkOnPrimaryColor, // Usando variable
    onSecondary = DarkOnSecondaryColor, // Usando variable
    onTertiary = DarkOnTertiaryColor, // Usando variable

    // Colores de texto/iconos SOBRE el fondo/superficie (Deben ser colores CLAROS)
    onBackground = DarkPrimaryTextColor, // Texto principal (Gris claro)
    onSurface = DarkPrimaryTextColor,
    onSurfaceVariant = DarkSecondaryTextColor, // TEXTO SECUNDARIO AJUSTADO

    // Color de contorno o para hints muy ligeros (Gris muy sutil)
    outline = DarkHintTextColor, // HINT TEXT COLOR AJUSTADO
)

@Composable
fun DriverAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    /*val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }*/
    val colorScheme = when {
        // ... (código para Android 12+)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MyTypography(),
        content = content
    )
}