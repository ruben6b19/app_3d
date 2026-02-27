package com.jaco.cc3d.domain.models

data class CreateAttemptResult(
    val attempt: StudentQuizAttempt,
    val questions: List<QuizQuestion>
)