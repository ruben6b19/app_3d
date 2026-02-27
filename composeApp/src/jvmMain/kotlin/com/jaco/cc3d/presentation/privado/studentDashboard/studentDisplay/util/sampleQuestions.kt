package com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.util


import com.jaco.cc3d.domain.models.QuizOption
import com.jaco.cc3d.domain.models.QuizQuestion
import java.util.Date

// Importante: Asegúrate de tener importado java.util.Date
object QuizDataProvider {
    fun getSampleQuestions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "q1",
                quizTemplateId = "template_01",
                questionText = "¿Cuál es la capital de Francia?",
                questionType = 1,
                options = listOf(
                    QuizOption("Londres", false),
                    QuizOption("Madrid", false),
                    QuizOption("París", true),
                    QuizOption("Roma", false)
                ),
                createdBy = "admin",
                status = 1,
                createdAt = Date(),
                updatedAt = Date()
            ),
            QuizQuestion(
                id = "q2",
                quizTemplateId = "template_01",
                questionText = "¿Cuál es el planeta más grande de nuestro sistema solar?",
                questionType = 1,
                options = listOf(
                    QuizOption("Marte", false),
                    QuizOption("Júpiter", true),
                    QuizOption("Saturno", false),
                    QuizOption("Neptuno", false)
                ),
                createdBy = "admin",
                status = 1,
                createdAt = java.util.Date(),
                updatedAt = java.util.Date()
            ),
            QuizQuestion(
                id = "q3",
                quizTemplateId = "template_01",
                questionText = "¿Quién pintó la 'Mona Lisa'?",
                questionType = 1,
                options = listOf(
                    QuizOption("Vincent van Gogh", false),
                    QuizOption("Pablo Picasso", false),
                    QuizOption("Leonardo da Vinci", true),
                    QuizOption("Claude Monet", false)
                ),
                createdBy = "admin",
                status = 1,
                createdAt = java.util.Date(),
                updatedAt = java.util.Date()
            ),
            QuizQuestion(
                id = "q4",
                quizTemplateId = "template_01",
                questionText = "¿Qué elemento químico tiene el símbolo 'O'?",
                questionType = 1,
                options = listOf(
                    QuizOption("Oro", false),
                    QuizOption("Osmio", false),
                    QuizOption("Oxígeno", true),
                    QuizOption("Hierro", false)
                ),
                createdBy = "admin",
                status = 1,
                createdAt = java.util.Date(),
                updatedAt = java.util.Date()
            )
        )
    }
}