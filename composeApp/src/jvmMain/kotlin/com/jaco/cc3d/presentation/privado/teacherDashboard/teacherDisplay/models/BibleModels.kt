package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models

data class ParsedCitation(val book: String, val queries: List<ChapterQuery>)
data class ChapterQuery(val chapter: Int, val verses: List<VerseCondition> = emptyList())
data class VerseCondition(val startVerse: String, val endVerse: String? = null)