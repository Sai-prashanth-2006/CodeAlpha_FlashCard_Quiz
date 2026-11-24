package com.codealpha.flashcards

/** A four-choice question belonging to one of the app's study topics. */
data class QuizQuestion(
    val id: String,
    val topic: String,
    val prompt: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
)
