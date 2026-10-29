package com.example.calcmaster.lumenflow.model

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

/**
 * Shared in-memory offline repository for Lumen Flow learning records,
 * Mistake Journal entries, and Concept Replay insights.
 */
object LumenLearningRepository {
    private val _mistakes = mutableStateListOf<SavedMistake>().apply {
        addAll(LumenCurriculumData.sampleMistakes)
    }

    val mistakes: List<SavedMistake> get() = _mistakes

    fun addMistake(
        question: String,
        studentAnswer: String,
        verifiedAnswer: String,
        errorConcept: String,
        explanation: String
    ): SavedMistake {
        val newMistake = SavedMistake(
            id = "mistake_${UUID.randomUUID().toString().take(8)}",
            question = question,
            studentAnswer = studentAnswer,
            verifiedAnswer = verifiedAnswer,
            errorConcept = errorConcept,
            explanation = explanation,
            timestamp = System.currentTimeMillis(),
            reviewCount = 0
        )
        _mistakes.add(0, newMistake)
        return newMistake
    }

    fun incrementReview(id: String) {
        val index = _mistakes.indexOfFirst { it.id == id }
        if (index != -1) {
            val current = _mistakes[index]
            _mistakes[index] = current.copy(reviewCount = current.reviewCount + 1)
        }
    }

    fun deleteMistake(id: String) {
        _mistakes.removeAll { it.id == id }
    }
}
