package com.codealpha.flashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

private val Canvas = Color(0xFFF5F6F1)
private val Ink = Color(0xFF1F302D)
private val MutedInk = Color(0xFF71807B)
private val Green = Color(0xFF2F6B5F)
private val PaleGreen = Color(0xFFE4EEE9)
private val CardWhite = Color(0xFFFFFEFB)
private val Border = Color(0xFFDDE4DE)
private val CorrectGreen = Color(0xFF28734E)
private val CorrectBackground = Color(0xFFE7F3EA)
private val IncorrectRed = Color(0xFF9B4C43)
private val IncorrectBackground = Color(0xFFF9EAE7)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecallTheme {
                QuizApp()
            }
        }
    }
}

@Composable
private fun RecallTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Green,
            onPrimary = Color.White,
            secondary = Color(0xFF6D8B7B),
            background = Canvas,
            surface = CardWhite,
            onSurface = Ink,
            onBackground = Ink,
            outline = Border,
        ),
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { QuizQuestionRepository(context.applicationContext) }
    var questions by remember { mutableStateOf(repository.loadQuestions()) }
    var selectedTopic by remember { mutableStateOf<String?>(null) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isEditorOpen by remember { mutableStateOf(false) }
    var questionBeingEdited by remember { mutableStateOf<QuizQuestion?>(null) }
    var defaultEditorTopic by remember { mutableStateOf(QuestionBank.topics.first()) }
    var questionPendingDelete by remember { mutableStateOf<QuizQuestion?>(null) }

    fun saveQuestionSet(updatedQuestions: List<QuizQuestion>) {
        questions = updatedQuestions
        repository.saveQuestions(updatedQuestions)
        selectedOption = null
    }

    fun openNewQuestionEditor() {
        questionBeingEdited = null
        defaultEditorTopic = selectedTopic ?: QuestionBank.topics.first()
        isEditorOpen = true
    }

    val activeTopicQuestions = questions.filter { it.topic == selectedTopic }
    val activeQuestion = activeTopicQuestions.getOrNull(currentIndex)

    Scaffold(
        containerColor = Canvas,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (selectedTopic != null) {
                        TextButton(
                            onClick = {
                                selectedTopic = null
                                currentIndex = 0
                                selectedOption = null
                            },
                        ) {
                            Text("‹ Topics", color = Green, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Green),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("R", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(11.dp))
                        Column {
                            Text("Recall", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Ink)
                            Text("Practice one question at a time", fontSize = 11.sp, color = MutedInk)
                        }
                    }
                },
                actions = {
                    TextButton(onClick = { openNewQuestionEditor() }) {
                        Text("＋ Add", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(6.dp))
                },
            )
        },
    ) { scaffoldPadding ->
        when {
            selectedTopic == null -> {
                TopicSelectionScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding),
                    questions = questions,
                    onTopicSelected = { topic ->
                        selectedTopic = topic
                        currentIndex = 0
                        selectedOption = null
                    },
                )
            }

            activeQuestion == null -> {
                EmptyTopicScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding),
                    topic = selectedTopic.orEmpty(),
                    onAddQuestion = { openNewQuestionEditor() },
                )
            }

            else -> {
                QuizQuestionScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding),
                    topic = selectedTopic.orEmpty(),
                    question = activeQuestion,
                    index = currentIndex,
                    topicQuestionCount = activeTopicQuestions.size,
                    selectedOption = selectedOption,
                    onSelectOption = { optionIndex ->
                        if (selectedOption == null) selectedOption = optionIndex
                    },
                    onPrevious = {
                        currentIndex -= 1
                        selectedOption = null
                    },
                    onNext = {
                        currentIndex += 1
                        selectedOption = null
                    },
                    onEdit = {
                        questionBeingEdited = activeQuestion
                        defaultEditorTopic = activeQuestion.topic
                        isEditorOpen = true
                    },
                    onDelete = { questionPendingDelete = activeQuestion },
                )
            }
        }
    }

    if (isEditorOpen) {
        QuestionEditorDialog(
            initialQuestion = questionBeingEdited,
            defaultTopic = defaultEditorTopic,
            onDismiss = { isEditorOpen = false },
            onSave = { topic, prompt, options, correctOptionIndex, explanation ->
                val editedQuestion = questionBeingEdited
                val savedQuestion = QuizQuestion(
                    id = editedQuestion?.id ?: UUID.randomUUID().toString(),
                    topic = topic,
                    prompt = prompt,
                    options = options,
                    correctOptionIndex = correctOptionIndex,
                    explanation = explanation,
                )
                val updatedQuestions = if (editedQuestion == null) {
                    questions + savedQuestion
                } else {
                    questions.map { question ->
                        if (question.id == editedQuestion.id) savedQuestion else question
                    }
                }
                saveQuestionSet(updatedQuestions)
                selectedTopic = topic
                currentIndex = updatedQuestions.indexOfLast { it.id == savedQuestion.id && it.topic == topic }
                    .coerceAtLeast(0)
                isEditorOpen = false
            },
        )
    }

    questionPendingDelete?.let { question ->
        AlertDialog(
            onDismissRequest = { questionPendingDelete = null },
            title = { Text("Delete this question?") },
            text = { Text("\"${question.prompt}\" will be removed from the ${question.topic} quiz.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val deletedIndex = currentIndex
                        val updatedQuestions = questions.filterNot { it.id == question.id }
                        saveQuestionSet(updatedQuestions)
                        val remainingInTopic = updatedQuestions.count { it.topic == question.topic }
                        currentIndex = if (remainingInTopic == 0) 0 else deletedIndex.coerceAtMost(remainingInTopic - 1)
                        questionPendingDelete = null
                    },
                ) {
                    Text("Delete", color = IncorrectRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { questionPendingDelete = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun TopicSelectionScreen(
    modifier: Modifier = Modifier,
    questions: List<QuizQuestion>,
    onTopicSelected: (String) -> Unit,
) {
    val questionCounts = questions.groupingBy { it.topic }.eachCount()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 24.dp, bottom = 30.dp),
    ) {
        Text("CHOOSE A TOPIC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedInk, letterSpacing = 1.4.sp)
        Spacer(Modifier.height(7.dp))
        Text("What would you like to practice?", fontSize = 23.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, color = Ink)
        Spacer(Modifier.height(8.dp))
        Text("Every topic starts with 20 questions and four choices per question.", fontSize = 14.sp, lineHeight = 21.sp, color = MutedInk)
        Spacer(Modifier.height(22.dp))

        QuestionBank.topics.chunked(2).forEach { topicRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                topicRow.forEach { topic ->
                    TopicTile(
                        topic = topic,
                        questionCount = questionCounts[topic] ?: 0,
                        modifier = Modifier.weight(1f),
                        onClick = { onTopicSelected(topic) },
                    )
                }
                if (topicRow.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TopicTile(
    topic: String,
    questionCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val abbreviation = when (topic) {
        "Cloud" -> "CL"
        "AI-ML" -> "AI"
        "Backend" -> "BE"
        "System Design" -> "SD"
        "HTML" -> "HT"
        "CSS" -> "CS"
        else -> "JS"
    }
    Surface(
        modifier = modifier
            .heightIn(min = 138.dp)
            .clickable(onClick = onClick),
        color = CardWhite,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Border),
        shadowElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PaleGreen),
                contentAlignment = Alignment.Center,
            ) {
                Text(abbreviation, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Green)
            }
            Spacer(Modifier.height(13.dp))
            Text(topic, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(3.dp))
            Text("$questionCount questions", fontSize = 12.sp, color = MutedInk)
        }
    }
}

@Composable
private fun QuizQuestionScreen(
    modifier: Modifier = Modifier,
    topic: String,
    question: QuizQuestion,
    index: Int,
    topicQuestionCount: Int,
    selectedOption: Int?,
    onSelectOption: (Int) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val isAnswered = selectedOption != null
    val isCorrect = selectedOption == question.correctOptionIndex

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 18.dp, bottom = 26.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(topic.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedInk, letterSpacing = 1.4.sp)
                Spacer(Modifier.height(4.dp))
                Text("Choose the best answer.", fontSize = 14.sp, color = Ink)
            }
            Text(
                text = "${index + 1} / $topicQuestionCount",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PaleGreen)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Green,
            )
        }
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { (index + 1f) / topicQuestionCount },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = Green,
            trackColor = Border,
        )
        Spacer(Modifier.height(20.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardWhite,
            shape = RoundedCornerShape(26.dp),
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, Border),
        ) {
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp)) {
                Text("QUESTION ${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Green, letterSpacing = 1.3.sp)
                Spacer(Modifier.height(14.dp))
                Text(question.prompt, fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold, color = Ink)
            }
        }

        Spacer(Modifier.height(16.dp))
        question.options.forEachIndexed { optionIndex, option ->
            AnswerOption(
                index = optionIndex,
                text = option,
                isAnswered = isAnswered,
                selectedOption = selectedOption,
                correctOptionIndex = question.correctOptionIndex,
                onClick = { onSelectOption(optionIndex) },
            )
            Spacer(Modifier.height(9.dp))
        }

        if (selectedOption != null) {
            FeedbackPanel(
                isCorrect = isCorrect,
                correctAnswer = question.options[question.correctOptionIndex],
                explanation = question.explanation,
            )
            Spacer(Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = index > 0,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(15.dp),
            ) {
                Text("←  Previous", fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onNext,
                enabled = index < topicQuestionCount - 1,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(15.dp),
            ) {
                Text("Next  →", fontWeight = FontWeight.SemiBold)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onEdit) { Text("Edit question", fontWeight = FontWeight.SemiBold) }
            Text("·", color = MutedInk)
            TextButton(onClick = onDelete) { Text("Delete", color = IncorrectRed, fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun AnswerOption(
    index: Int,
    text: String,
    isAnswered: Boolean,
    selectedOption: Int?,
    correctOptionIndex: Int,
    onClick: () -> Unit,
) {
    val isCorrectChoice = isAnswered && index == correctOptionIndex
    val isWrongSelection = selectedOption == index && index != correctOptionIndex
    val fillColor = when {
        isCorrectChoice -> CorrectBackground
        isWrongSelection -> IncorrectBackground
        else -> CardWhite
    }
    val outlineColor = when {
        isCorrectChoice -> CorrectGreen
        isWrongSelection -> IncorrectRed
        else -> Border
    }
    val markerColor = when {
        isCorrectChoice -> CorrectGreen
        isWrongSelection -> IncorrectRed
        else -> MutedInk
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(fillColor)
            .border(1.dp, outlineColor, RoundedCornerShape(17.dp))
            .clickable(enabled = !isAnswered, onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(if (isCorrectChoice || isWrongSelection) markerColor else PaleGreen),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isCorrectChoice) "✓" else if (isWrongSelection) "×" else ('A' + index).toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCorrectChoice || isWrongSelection) Color.White else Green,
            )
        }
        Spacer(Modifier.width(13.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Ink,
        )
    }
}

@Composable
private fun FeedbackPanel(isCorrect: Boolean, correctAnswer: String, explanation: String) {
    val accent = if (isCorrect) CorrectGreen else IncorrectRed
    val background = if (isCorrect) CorrectBackground else IncorrectBackground
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = background,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.25f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                if (isCorrect) "Correct!" else "Not quite",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accent,
            )
            if (!isCorrect) {
                Spacer(Modifier.height(4.dp))
                Text("Correct answer: $correctAnswer", fontSize = 13.sp, lineHeight = 19.sp, color = Ink)
            }
            if (explanation.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(explanation, fontSize = 13.sp, lineHeight = 19.sp, color = Ink)
            }
        }
    }
}

@Composable
private fun EmptyTopicScreen(modifier: Modifier = Modifier, topic: String, onAddQuestion: () -> Unit) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("No $topic questions yet", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Ink, textAlign = TextAlign.Center)
        Spacer(Modifier.height(9.dp))
        Text("Add a question with four choices to start this quiz.", fontSize = 15.sp, lineHeight = 22.sp, color = MutedInk, textAlign = TextAlign.Center)
        Spacer(Modifier.height(22.dp))
        Button(onClick = onAddQuestion, shape = RoundedCornerShape(15.dp)) {
            Text("Add a question", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun QuestionEditorDialog(
    initialQuestion: QuizQuestion?,
    defaultTopic: String,
    onDismiss: () -> Unit,
    onSave: (topic: String, prompt: String, options: List<String>, correctOptionIndex: Int, explanation: String) -> Unit,
) {
    var topic by remember(initialQuestion?.id) { mutableStateOf(initialQuestion?.topic ?: defaultTopic) }
    var prompt by remember(initialQuestion?.id) { mutableStateOf(initialQuestion?.prompt.orEmpty()) }
    var optionA by remember(initialQuestion?.id) { mutableStateOf(initialQuestion?.options?.getOrNull(0).orEmpty()) }
    var optionB by remember(initialQuestion?.id) { mutableStateOf(initialQuestion?.options?.getOrNull(1).orEmpty()) }
    var optionC by remember(initialQuestion?.id) { mutableStateOf(initialQuestion?.options?.getOrNull(2).orEmpty()) }
    var optionD by remember(initialQuestion?.id) { mutableStateOf(initialQuestion?.options?.getOrNull(3).orEmpty()) }
    var correctOptionIndex by remember(initialQuestion?.id) { mutableIntStateOf(initialQuestion?.correctOptionIndex ?: 0) }
    var explanation by remember(initialQuestion?.id) { mutableStateOf(initialQuestion?.explanation.orEmpty()) }
    var topicMenuExpanded by remember { mutableStateOf(false) }
    var hasValidationError by remember { mutableStateOf(false) }
    val optionValues = listOf(optionA, optionB, optionC, optionD)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialQuestion == null) "Create a question" else "Edit question") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(11.dp),
            ) {
                Text("Choose a topic, enter four choices, then mark the correct one.", color = MutedInk, fontSize = 13.sp, lineHeight = 19.sp)
                Box {
                    OutlinedButton(
                        onClick = { topicMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Topic: $topic", modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                        Text("▾")
                    }
                    DropdownMenu(
                        expanded = topicMenuExpanded,
                        onDismissRequest = { topicMenuExpanded = false },
                    ) {
                        QuestionBank.topics.forEach { topicOption ->
                            DropdownMenuItem(
                                text = { Text(topicOption) },
                                onClick = {
                                    topic = topicOption
                                    topicMenuExpanded = false
                                },
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Question") },
                    minLines = 2,
                    maxLines = 4,
                    isError = hasValidationError && prompt.isBlank(),
                )

                optionValues.forEachIndexed { index, value ->
                    Column {
                        OutlinedTextField(
                            value = value,
                            onValueChange = { newValue ->
                                when (index) {
                                    0 -> optionA = newValue
                                    1 -> optionB = newValue
                                    2 -> optionC = newValue
                                    else -> optionD = newValue
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Option ${'A' + index}") },
                            minLines = 1,
                            maxLines = 3,
                            isError = hasValidationError && value.isBlank(),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { correctOptionIndex = index }
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = correctOptionIndex == index,
                                onClick = { correctOptionIndex = index },
                            )
                            Text("Mark as correct answer", fontSize = 13.sp, color = MutedInk)
                        }
                    }
                }

                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Explanation (optional)") },
                    minLines = 2,
                    maxLines = 4,
                )
                if (hasValidationError) {
                    val optionsAreUnique = optionValues.map(String::trim).distinct().size == optionValues.size
                    Text(
                        text = if (!optionsAreUnique) "Each of the four choices must be different."
                        else "Enter a question and all four choices.",
                        color = IncorrectRed,
                        fontSize = 12.sp,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedOptions = optionValues.map(String::trim)
                    if (prompt.isBlank() || trimmedOptions.any(String::isBlank) || trimmedOptions.distinct().size != 4) {
                        hasValidationError = true
                    } else {
                        onSave(topic, prompt.trim(), trimmedOptions, correctOptionIndex, explanation.trim())
                    }
                },
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Save question")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
