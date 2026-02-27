package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models


import androidx.compose.ui.graphics.Color

data class SlideContent(
    val id: String,
    val title: String,
    val contentText: String, // Simplificado para este ejemplo
    val backgroundColor: Color = Color.White,
    val citations: List<String> = emptyList(),
    val masterSlideIndex: Int,
    val totalMasterSlides: Int = 0,
    val imageUrl: String? = null,
    val svgRawCode: String? = null
)

