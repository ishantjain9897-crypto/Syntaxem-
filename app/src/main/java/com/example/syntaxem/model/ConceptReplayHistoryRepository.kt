package com.example.syntaxem.model

import androidx.compose.runtime.mutableStateListOf
import com.example.core.calculation.model.Result
import java.util.UUID

/**
 * The 5 phases in Syntaxem's signature learning cycle:
 * SOLVE -> UNDERSTAND -> EXPERIMENT -> APPLY -> REMEMBER
 */
enum class ReplayPhase(val label: String, val description: String) {
    SOLVE("1. SOLVE", "Accurate offline scientific computation & verification"),
    UNDERSTAND("2. UNDERSTAND", "Step-by-step derivation & 'Why this step?' axioms"),
    EXPERIMENT("3. EXPERIMENT", "Visual laboratory simulation or interactive graph"),
    APPLY("4. APPLY", "Topic-matched concept challenge or mini-game"),
    REMEMBER("5. REMEMBER", "Low-pressure retrieval practice & mistake journal")
}

/**
 * A persistent local session in the student's offline Concept Replay history.
 */
data class ConceptReplaySession(
    val id: String,
    val expression: String,
    val exactValue: String,
    val decimalValue: Double?,
    val decimalFormatted: String,
    val topicName: String,
    val timestamp: Long,
    val stepsCount: Int,
    val currentPhase: ReplayPhase = ReplayPhase.SOLVE,
    val studentNotes: String? = null
)

/**
 * 100% offline local repository tracking student calculations and the 5-phase
 * Concept Replay loop without any online trackers or network dependencies.
 */
object ConceptReplayHistoryRepository {

    private val _sessions = mutableStateListOf<ConceptReplaySession>().apply {
        // Pre-populate with authentic Class 11 learning sessions
        add(
            ConceptReplaySession(
                id = "sess_001",
                expression = "20^2 * sin(2 * 45) / 9.8",
                exactValue = "400/9.8",
                decimalValue = 40.8163,
                decimalFormatted = "40.8163",
                topicName = "Motion in a Plane: Projectiles",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                stepsCount = 4,
                currentPhase = ReplayPhase.EXPERIMENT,
                studentNotes = "Max horizontal range occurs at launch angle θ = 45° where sin(2θ) = 1"
            )
        )
        add(
            ConceptReplaySession(
                id = "sess_002",
                expression = "sin(30) + cos(60)",
                exactValue = "1",
                decimalValue = 1.0,
                decimalFormatted = "1",
                topicName = "Trigonometry & Unit Circle",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                stepsCount = 3,
                currentPhase = ReplayPhase.APPLY,
                studentNotes = "Co-function identity: sin(30°) = cos(90° - 30°) = cos(60°) = 1/2"
            )
        )
        add(
            ConceptReplaySession(
                id = "sess_003",
                expression = "x^2 - 5*x + 6",
                exactValue = "(x - 2)(x - 3)",
                decimalValue = 0.0,
                decimalFormatted = "Roots: 2, 3",
                topicName = "Quadratic Equations & Roots",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 360,
                stepsCount = 3,
                currentPhase = ReplayPhase.UNDERSTAND,
                studentNotes = "Discriminant D = b² - 4ac = 25 - 24 = 1 > 0; two distinct real roots"
            )
        )
    }

    val sessions: List<ConceptReplaySession> get() = _sessions

    fun recordCalculation(
        expression: String,
        result: Result,
        topicName: String
    ): ConceptReplaySession {
        // Check if expression is already the most recent session
        val existingIndex = _sessions.indexOfFirst { it.expression == expression }
        if (existingIndex == 0) {
            return _sessions[0]
        }

        val session = ConceptReplaySession(
            id = "sess_${UUID.randomUUID().toString().take(8)}",
            expression = expression,
            exactValue = result.exactValue,
            decimalValue = result.decimalValue,
            decimalFormatted = result.decimalFormatted,
            topicName = topicName,
            timestamp = System.currentTimeMillis(),
            stepsCount = result.steps.size,
            currentPhase = if (result.steps.isNotEmpty()) ReplayPhase.UNDERSTAND else ReplayPhase.SOLVE
        )

        if (existingIndex > 0) {
            _sessions.removeAt(existingIndex)
        }
        _sessions.add(0, session)
        return session
    }

    fun advancePhase(sessionId: String, newPhase: ReplayPhase) {
        val index = _sessions.indexOfFirst { it.id == sessionId }
        if (index != -1) {
            val curr = _sessions[index]
            _sessions[index] = curr.copy(currentPhase = newPhase)
        }
    }

    fun attachNote(sessionId: String, note: String) {
        val index = _sessions.indexOfFirst { it.id == sessionId }
        if (index != -1) {
            val curr = _sessions[index]
            _sessions[index] = curr.copy(studentNotes = note)
        }
    }

    fun removeSession(sessionId: String) {
        _sessions.removeAll { it.id == sessionId }
    }

    fun clearAll() {
        _sessions.clear()
    }
}
