package com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.domain.models.QuizQuestion
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions.util.QuizQuestionListStrings

@Composable
fun QuizQuestionList(viewModel: QuizQuestionViewModel, texts: QuizQuestionListStrings) {
    if (viewModel.questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(texts.emptyListMessage)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(viewModel.questions) { question ->
                QuestionCard(question, viewModel, texts)
            }
        }
    }
}

@Composable
fun QuestionCard(question: QuizQuestion, viewModel: QuizQuestionViewModel, texts: QuizQuestionListStrings) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(question.questionText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${texts.optionsCountPrefix} ${question.options.size}", style = MaterialTheme.typography.bodySmall)
            }

            Box {
                IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, null) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text(texts.editAction) },
                        onClick = { viewModel.openEditForm(question); expanded = false },
                        leadingIcon = { Icon(Icons.Default.Edit, null) }
                    )
                    DropdownMenuItem(
                        text = { Text(texts.deleteAction, color = MaterialTheme.colorScheme.error) },
                        onClick = { viewModel.deleteQuestion(question.id); expanded = false },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    }
}