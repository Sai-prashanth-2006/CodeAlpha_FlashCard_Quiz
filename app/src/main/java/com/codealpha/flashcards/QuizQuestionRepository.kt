package com.codealpha.flashcards

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Stores the quiz bank and user edits in app-private preferences. */
class QuizQuestionRepository(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadQuestions(): List<QuizQuestion> {
        val storedQuestions = preferences.getString(KEY_QUESTIONS, null)
        if (storedQuestions == null) {
            return QuestionBank.starterQuestions.also(::saveQuestions)
        }

        return runCatching {
            val jsonQuestions = JSONArray(storedQuestions)
            buildList {
                for (index in 0 until jsonQuestions.length()) {
                    val jsonQuestion = jsonQuestions.optJSONObject(index) ?: continue
                    val topic = jsonQuestion.optString(FIELD_TOPIC)
                    val prompt = jsonQuestion.optString(FIELD_PROMPT).trim()
                    val explanation = jsonQuestion.optString(FIELD_EXPLANATION).trim()
                    val optionsJson = jsonQuestion.optJSONArray(FIELD_OPTIONS) ?: continue
                    val options = buildList {
                        for (optionIndex in 0 until optionsJson.length()) {
                            add(optionsJson.optString(optionIndex).trim())
                        }
                    }
                    val correctOptionIndex = jsonQuestion.optInt(FIELD_CORRECT_OPTION, INVALID_INDEX)

                    if (topic in QuestionBank.topics &&
                        prompt.isNotEmpty() &&
                        options.size == OPTION_COUNT &&
                        options.all(String::isNotEmpty) &&
                        options.distinct().size == OPTION_COUNT &&
                        correctOptionIndex in options.indices
                    ) {
                        add(
                            QuizQuestion(
                                id = jsonQuestion.optString(FIELD_ID).ifBlank { "$topic-${index + 1}" },
                                topic = topic,
                                prompt = prompt,
                                options = options,
                                correctOptionIndex = correctOptionIndex,
                                explanation = explanation,
                            ),
                        )
                    }
                }
            }
        }.getOrElse {
            QuestionBank.starterQuestions.also(::saveQuestions)
        }
    }

    fun saveQuestions(questions: List<QuizQuestion>) {
        val jsonQuestions = JSONArray()
        questions.forEach { question ->
            jsonQuestions.put(
                JSONObject()
                    .put(FIELD_ID, question.id)
                    .put(FIELD_TOPIC, question.topic)
                    .put(FIELD_PROMPT, question.prompt)
                    .put(FIELD_OPTIONS, JSONArray(question.options))
                    .put(FIELD_CORRECT_OPTION, question.correctOptionIndex)
                    .put(FIELD_EXPLANATION, question.explanation),
            )
        }
        preferences.edit().putString(KEY_QUESTIONS, jsonQuestions.toString()).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "recall_flashcards"
        const val KEY_QUESTIONS = "quiz_questions_v2"
        const val FIELD_ID = "id"
        const val FIELD_TOPIC = "topic"
        const val FIELD_PROMPT = "prompt"
        const val FIELD_OPTIONS = "options"
        const val FIELD_CORRECT_OPTION = "correct_option"
        const val FIELD_EXPLANATION = "explanation"
        const val OPTION_COUNT = 4
        const val INVALID_INDEX = -1
    }
}
