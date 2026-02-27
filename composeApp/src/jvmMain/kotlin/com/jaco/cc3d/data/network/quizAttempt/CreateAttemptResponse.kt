package com.jaco.cc3d.data.network.quizAttempt

import com.jaco.cc3d.data.network.quizQuestion.QuizQuestionDto
import kotlinx.serialization.Serializable

@Serializable
data class CreateAttemptResponse(
    val attempt: StudentQuizAttemptDto,
    val questions: List<QuizQuestionDto>
)