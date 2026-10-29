package com.example.calcmaster.lumenflow.model

/**
 * Subject category for CBSE Class 11 curriculum.
 */
enum class LumenSubject(val displayName: String, val subtitle: String) {
    MATHEMATICS("Mathematics", "Functions, Algebra, Calculus & Geometry"),
    TRIGONOMETRY("Trigonometry", "Unit Circle, Identities & Periodic Waves"),
    PHYSICS("Physics", "Kinematics, Mechanics, Gravitation & Oscillations")
}

/**
 * Topic node in the curriculum tree.
 */
data class CurriculumTopic(
    val id: String,
    val subject: LumenSubject,
    val title: String,
    val chapter: String,
    val learningObjective: String,
    val prerequisites: List<String>,
    val conceptExplanation: String,
    val governingFormulae: List<String>,
    val workedExamples: List<WorkedExample>,
    val masteryLevel: Float = 0.0f, // 0.0 to 1.0
    val needsReview: Boolean = false
)

data class WorkedExample(
    val problem: String,
    val steps: List<String>,
    val finalAnswer: String,
    val verifiedRule: String
)

/**
 * Mistake Journal record for student reflection and spaced review.
 */
data class SavedMistake(
    val id: String,
    val question: String,
    val studentAnswer: String,
    val verifiedAnswer: String,
    val errorConcept: String,
    val explanation: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reviewCount: Int = 0
)

/**
 * Personal learning goal.
 */
data class LearningGoal(
    val id: String,
    val title: String,
    val targetTopic: String,
    val progress: Float, // 0.0 to 1.0
    val targetDate: String
)

/**
 * Contextual AI tutor inquiry response.
 */
data class TutorInsight(
    val queryMode: String,
    val content: String,
    val verifiedCalculation: String? = null,
    val formulaBadge: String? = null,
    val commonMistakeNote: String? = null
)

/**
 * Shipped curriculum dataset for Class 11 CBSE/NCERT.
 */
object LumenCurriculumData {
    val topics = listOf(
        CurriculumTopic(
            id = "math_quadratic",
            subject = LumenSubject.MATHEMATICS,
            title = "Quadratic Equations & Complex Roots",
            chapter = "Chapter 4: Complex Numbers and Quadratic Equations",
            learningObjective = "Determine real and complex roots of ax² + bx + c = 0 using the quadratic formula and discriminant D = b² - 4ac.",
            prerequisites = listOf("Linear algebraic manipulation", "Real number arithmetic"),
            conceptExplanation = "A quadratic equation is a second-order polynomial equation. The nature of its roots depends strictly on the discriminant D = b² - 4ac. If D > 0, there are two distinct real roots. If D = 0, there is one repeated real root. If D < 0, the roots are complex conjugates x = (-b ± i√|D|) / (2a).",
            governingFormulae = listOf("x = (-b ± √(b² - 4ac)) / (2a)", "D = b² - 4ac"),
            workedExamples = listOf(
                WorkedExample(
                    problem = "Solve x² - 5x + 6 = 0",
                    steps = listOf(
                        "Identify coefficients: a = 1, b = -5, c = 6",
                        "Compute discriminant: D = (-5)² - 4(1)(6) = 25 - 24 = 1 > 0",
                        "Apply formula: x = (5 ± √1) / 2 = (5 ± 1) / 2",
                        "Roots: x₁ = 3, x₂ = 2"
                    ),
                    finalAnswer = "x = 2 or x = 3",
                    verifiedRule = "Quadratic Factorization & Formula"
                )
            ),
            masteryLevel = 0.85f,
            needsReview = false
        ),
        CurriculumTopic(
            id = "trig_unit_circle",
            subject = LumenSubject.TRIGONOMETRY,
            title = "Unit Circle & Trigonometric Coordinates",
            chapter = "Chapter 3: Trigonometric Functions",
            learningObjective = "Define trigonometric ratios for arbitrary angles via circle coordinates (cos θ, sin θ) and understand sign conventions across the four quadrants.",
            prerequisites = listOf("Right-angled triangle ratios", "Pythagorean theorem"),
            conceptExplanation = "On a circle of radius r = 1 centered at origin (0, 0), any angle θ in standard position intersects the circle at (x, y) = (cos θ, sin θ). This definition extends sine and cosine beyond [0°, 90°] to all real angles. Quadrant I: all positive. Quadrant II: sin positive. Quadrant III: tan positive. Quadrant IV: cos positive (ASTC rule).",
            governingFormulae = listOf("cos² θ + sin² θ = 1", "tan θ = sin θ / cos θ", "sin(180° - θ) = sin θ"),
            workedExamples = listOf(
                WorkedExample(
                    problem = "Evaluate sin(150°) and cos(150°)",
                    steps = listOf(
                        "150° lies in Quadrant II, where reference angle is 180° - 150° = 30°",
                        "In Quadrant II, sine is positive: sin(150°) = sin(30°) = 1/2",
                        "In Quadrant II, cosine is negative: cos(150°) = -cos(30°) = -√3/2"
                    ),
                    finalAnswer = "sin(150°) = 1/2, cos(150°) = -√3/2",
                    verifiedRule = "Quadrant Angle Symmetry"
                )
            ),
            masteryLevel = 0.70f,
            needsReview = true
        ),
        CurriculumTopic(
            id = "physics_kinematics_2d",
            subject = LumenSubject.PHYSICS,
            title = "Motion in a Plane: Projectile Trajectories",
            chapter = "Chapter 4: Motion in a Plane",
            learningObjective = "Analyze 2D motion with constant acceleration by separating orthogonal horizontal (uniform motion) and vertical (free-fall) components.",
            prerequisites = listOf("1D Kinematic equations: v = u + at, s = ut + ½at²", "Trigonometric vector resolution"),
            conceptExplanation = "In an idealized vacuum, the horizontal motion has zero acceleration (a_x = 0, v_x = v₀ cos θ = const). The vertical motion experiences constant gravitational acceleration downwards (a_y = -g). These two orthogonal motions operate independently. The trajectory curve is a parabola y = (tan θ)x - [g / (2v₀² cos² θ)] x².",
            governingFormulae = listOf(
                "R = (v₀² sin 2θ) / g",
                "H_max = (v₀ sin θ)² / (2g)",
                "T = (2v₀ sin θ) / g"
            ),
            workedExamples = listOf(
                WorkedExample(
                    problem = "A projectile is launched at v₀ = 20 m/s at θ = 30°. Find its range R and time of flight T (g = 9.8 m/s²).",
                    steps = listOf(
                        "Horizontal velocity: v₀x = 20 · cos(30°) = 20 · (√3/2) = 17.32 m/s",
                        "Vertical velocity: v₀y = 20 · sin(30°) = 20 · 0.5 = 10.0 m/s",
                        "Time of flight: T = 2(10) / 9.8 = 20 / 9.8 ≈ 2.04 s",
                        "Range: R = v₀x · T = 17.32 · 2.04 ≈ 35.35 m"
                    ),
                    finalAnswer = "T ≈ 2.04 s, R ≈ 35.35 m",
                    verifiedRule = "Orthogonal 2D Kinematics"
                )
            ),
            masteryLevel = 0.90f,
            needsReview = false
        ),
        CurriculumTopic(
            id = "physics_newton_laws",
            subject = LumenSubject.PHYSICS,
            title = "Laws of Motion & Friction",
            chapter = "Chapter 5: Laws of Motion",
            learningObjective = "Apply Newton's three laws of motion and static/kinetic friction laws to free-body diagrams in equilibrium and accelerated frames.",
            prerequisites = listOf("Vector addition & components", "Scalar vs vector quantities"),
            conceptExplanation = "Newton's first law defines inertial frames. Newton's second law establishes F_net = dp/dt = ma for constant mass. Newton's third law states action and reaction forces are equal in magnitude, opposite in direction, and act on different bodies. Friction opposes relative impending motion: f_s ≤ μ_s N and f_k = μ_k N.",
            governingFormulae = listOf("F_net = m · a", "f_s,max = μ_s · N", "p = m · v"),
            workedExamples = listOf(
                WorkedExample(
                    problem = "A 5 kg block rests on a horizontal table (μ_s = 0.4). Find minimum force to initiate motion (g = 9.8 m/s²).",
                    steps = listOf(
                        "Normal force: N = mg = 5 · 9.8 = 49.0 N",
                        "Maximum static friction: f_s,max = μ_s · N = 0.4 · 49.0 = 19.6 N",
                        "To initiate motion, applied horizontal force must exceed f_s,max."
                    ),
                    finalAnswer = "F_min = 19.6 N",
                    verifiedRule = "Static Friction Threshold"
                )
            ),
            masteryLevel = 0.60f,
            needsReview = true
        )
    )

    val sampleMistakes = listOf(
        SavedMistake(
            id = "mistake_01",
            question = "Evaluate the range multiplier when initial velocity v₀ is doubled from 15 m/s to 30 m/s at 45°.",
            studentAnswer = "Range doubles (2×)",
            verifiedAnswer = "Range quadruples (4×)",
            errorConcept = "Quadratic Velocity Dependence",
            explanation = "Range formula R = (v₀² sin 2θ) / g depends on the square of velocity (v₀²). When v₀ is multiplied by 2, R is multiplied by 2² = 4.",
            reviewCount = 1
        ),
        SavedMistake(
            id = "mistake_02",
            question = "Find the vertical velocity of a projectile at its maximum height.",
            studentAnswer = "v_y = 9.8 m/s",
            verifiedAnswer = "v_y = 0 m/s",
            errorConcept = "Apex Kinematic Boundary",
            explanation = "At the highest point, vertical motion instantaneously stops before descending. Thus v_y = 0. Acceleration downwards remains g = 9.8 m/s².",
            reviewCount = 2
        )
    )

    val learningGoals = listOf(
        LearningGoal(
            id = "goal_01",
            title = "Master Projectile Motion & Vector Orthogonality",
            targetTopic = "Motion in a Plane",
            progress = 0.75f,
            targetDate = "This Week"
        ),
        LearningGoal(
            id = "goal_02",
            title = "Complete Unit Circle Symmetry Exercises",
            targetTopic = "Trigonometric Functions",
            progress = 0.50f,
            targetDate = "Next Week"
        ),
        LearningGoal(
            id = "goal_03",
            title = "Verify Complex Quadratic Conjugate Pairs",
            targetTopic = "Complex Numbers",
            progress = 0.85f,
            targetDate = "Completed"
        )
    )
}
