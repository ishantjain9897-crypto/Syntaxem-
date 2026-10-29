package com.example.calcmaster.model

enum class AngleMode {
    DEG, RAD, GRAD
}

enum class ExplanationMode {
    HINT, OUTLINE, FULL_SOLUTION
}

data class DerivationStep(
    val stepNumber: Int,
    val ruleOrIdentity: String,
    val applicabilityReason: String,
    val substitution: String,
    val intermediateValue: String,
    val commonMistakeNote: String? = null
)

data class CalculationResult(
    val expression: String,
    val exactResult: String,
    val decimalResult: String,
    val steps: List<DerivationStep> = emptyList(),
    val domainNotice: String? = null,
    val conceptTag: String? = null,
    val linkedSimulation: String? = null,
    val linkedGame: String? = null,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)

data class HistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val expression: String,
    val result: String,
    val conceptTag: String? = null
)

data class ConceptMastery(
    val topicId: String,
    val topicName: String,
    val category: String, // "Mathematics", "Physics", "Trigonometry"
    val masteryScore: Float, // 0.0 to 1.0
    val totalAttempts: Int = 0,
    val correctAttempts: Int = 0,
    val lastReviewedTimestamp: Long = System.currentTimeMillis()
)
