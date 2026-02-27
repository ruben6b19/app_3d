package com.jaco.cc3d.presentation.app.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// =======================================================
// REFERENCIA DE PALETA PÚRPURA (SOLICITADA POR EL USUARIO)
// =======================================================
val PrimaryPurple = Color(0xFF673AB7)     // Análogo a PrimaryTeal (Base Light)
val LightPurple = Color(0xFFD1C4E9)    // Análogo a LightTeal (Primary Dark)
val DarkPurple = Color(0xFF3F2B66)     // ANÁLOGO A DARK TEAL (Tono más oscuro y saturado)
// 🆕 NUEVOS COLORES MAGENTA PARA SECONDARY
// =======================================================
val SecondaryMagenta = Color(0xFFE91E63)      // Magenta oscuro para Light Mode Secondary
val LightMagenta = Color(0xFFFF80AB)
// =======================================================
// COLORES BASE DEL TEMA
// =======================================================

// Colores de texto genéricos (se usan como base para los esquemas onSurface/onBackground)
val PrimaryTextColor = Color(0xFF333333) // Gris Oscuro (Texto principal para Light Mode)
val SecondaryTextColor = Color(0xFF666666) // Gris Medio (Texto secundario para Light Mode)
val HintTextColor = Color(0xFF999999)    // Gris Claro (Texto de Hint para Light Mode)
val AccentColor = Color(0xFF0DF802)      // Azul de acento



val PrimaryTeal = Color(0xFF673AB7)
val LightTeal = Color(0xFFD1C4E9) // Tono más claro para mejor visibilidad en Dark Mode
val DarkTeal = Color(0xFF005353)  // Tono más oscuro

// Colores base para Light Mode
val LightBackgroundSurfaceBase = Color(0xFFF8F8F8) // Fondo y superficie principal para Light Mode

// Colores base para Dark Mode
val DarkBackground = Color(0xFF121212)    // Fondo muy oscuro (Material 3)
// El color primario del texto en Dark Mode debe ser claro
//val DarkPrimaryTextColor = Color(0xFFE0E0E0)   // Gris muy claro (Texto principal en Dark Mode)
val DarkPrimaryTextColor = Color(0xFFE0E0E0)   // Gris muy claro (Texto principal en Dark Mode)
val DarkSecondaryTextColor = Color(0xFFAAAAAA) // Menos prominente que DarkPrimaryTextColor
val DarkHintTextColor = Color(0xFF757575)       // El más sutil de todos

// --- Nuevas variables para texto/iconos que van SOBRE los colores de marca ---
// Light Theme: Todos son blancos.
val LightOnPrimaryColor = Color.White
val LightOnSecondaryColor = Color.White
val LightOnTertiaryColor = Color.White

// Dark Theme: onSecondary es negro, los otros son blancos.
val DarkOnPrimaryColor = Color.White
val DarkOnSecondaryColor = Color.White
val DarkOnTertiaryColor = Color.White