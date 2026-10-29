package com.example.orbitlab.model

/**
 * CBSE Class 11 curriculum mission specification.
 */
data class LearningMission(
    val id: String,
    val title: String,
    val unit: String,
    val description: String,
    val targetObjective: String,
    val targetDistanceMeters: Double? = null,
    val allowedToleranceMeters: Double = 2.0,
    val defaultParameters: FlightParameters = FlightParameters(),
    val governingFormula: String,
    val keyConcept: String,
    val isCompleted: Boolean = false
)

/**
 * Inquiry questions for the AI Mission Assistant Predict-and-Verify workflow.
 */
data class PredictInquiry(
    val id: String,
    val question: String,
    val context: String,
    val options: List<String>,
    val correctIndex: Int,
    val deterministicExplanation: String,
    val modelVsRealExplanation: String,
    val formulaBadge: String
)

object OrbitLabCurriculum {
    val missions = listOf(
        LearningMission(
            id = "mission_orbital_target",
            title = "Orbital Drop: Parabolic Target Insertion",
            unit = "Unit II: Kinematics — Projectile Motion",
            description = "Calculate the exact launch velocity and angle required to land a supply capsule on target station Bravo at distance d = 50.0m on Earth.",
            targetObjective = "Deliver payload within ±2.0m of target at 50m.",
            targetDistanceMeters = 50.0,
            allowedToleranceMeters = 2.5,
            defaultParameters = FlightParameters(
                initialVelocity = 22.15,
                launchAngleDegrees = 45.0,
                gravity = 9.80665,
                celestialBody = CelestialBody.EARTH
            ),
            governingFormula = "R = (v₀² · sin(2θ)) / g",
            keyConcept = "Range is maximized at θ = 45° in vacuum. For a target at R, required v₀ = √(R · g / sin(2θ))."
        ),
        LearningMission(
            id = "mission_moon_range",
            title = "Lunar Traverse: Low-Gravity Ballistics",
            unit = "Unit VI: Gravitation & Free Fall",
            description = "Explore projectile flight on the Moon where g = 1.62 m/s² (1/6th Earth gravity). Observe how range and flight time scale inversely with g.",
            targetObjective = "Predict and verify the range multiplier when switching from Earth to Moon.",
            targetDistanceMeters = 150.0,
            allowedToleranceMeters = 5.0,
            defaultParameters = FlightParameters(
                initialVelocity = 20.0,
                launchAngleDegrees = 45.0,
                gravity = 1.62,
                celestialBody = CelestialBody.MOON
            ),
            governingFormula = "T = (2v₀ sin θ) / g,   R = (v₀² sin 2θ) / g",
            keyConcept = "Because g_moon ≈ g_earth / 6, time of flight and range increase roughly 6-fold for identical launch energy."
        ),
        LearningMission(
            id = "mission_complementary_angles",
            title = "Invariance Theorem: Complementary Launch Angles",
            unit = "Unit II: Kinematics & Trigonometry",
            description = "Compare trajectories launched at θ = 30° versus θ = 60° with the same initial velocity. Discover why both have identical horizontal range.",
            targetObjective = "Verify that sin(2θ) = sin(2(90° - θ)), producing equal ranges with different apex heights.",
            targetDistanceMeters = null,
            defaultParameters = FlightParameters(
                initialVelocity = 25.0,
                launchAngleDegrees = 30.0,
                gravity = 9.80665,
                celestialBody = CelestialBody.EARTH
            ),
            governingFormula = "sin(2(90° - θ)) = sin(180° - 2θ) = sin(2θ)",
            keyConcept = "Complementary angles (θ and 90° - θ) achieve the same range R, but the higher angle spends longer in flight and reaches greater apex."
        ),
        LearningMission(
            id = "mission_vector_resolution",
            title = "Vector Velocity Orthogonality",
            unit = "Unit II: Motion in a Plane — Vectors",
            description = "Observe that horizontal motion (v_x = v₀ cos θ) is completely uniform and unaffected by gravity, while vertical motion (v_y = v₀ sin θ - gt) experiences constant acceleration.",
            targetObjective = "Monitor instantaneous velocity vector v(t) = v_x î + (v_y - gt) ĵ at the apex where v_y = 0.",
            targetDistanceMeters = null,
            defaultParameters = FlightParameters(
                initialVelocity = 30.0,
                launchAngleDegrees = 53.13, // 3-4-5 triangle approximation
                gravity = 9.80665,
                celestialBody = CelestialBody.EARTH
            ),
            governingFormula = "v(t) = √(v_x² + v_y(t)²),   tan φ = v_y(t) / v_x",
            keyConcept = "At maximum height, vertical velocity component is zero (v_y = 0). The total speed equals purely the horizontal component v_x."
        )
    )

    val inquiries = listOf(
        PredictInquiry(
            id = "inq_velocity_doubling",
            question = "If launch velocity v₀ is doubled from 20 m/s to 40 m/s (holding launch angle θ = 45° constant), how does the total range R change?",
            context = "Consider the standard projectile range equation R = (v₀² sin 2θ) / g.",
            options = listOf(
                "Range doubles (2×)",
                "Range triples (3×)",
                "Range quadruples (4×)",
                "Range stays unchanged"
            ),
            correctIndex = 2,
            deterministicExplanation = "Because range R is proportional to the square of initial velocity (R ∝ v₀²), doubling velocity multiplies the range by 2² = 4. At 20 m/s, R = 40.79 m. At 40 m/s, R = 163.15 m.",
            modelVsRealExplanation = "Mathematical Model vs Reality: In our idealized vacuum model, R quadruples exactly. In the real world with air resistance, aerodynamic drag scales with v² (F_drag ∝ v²), causing high-velocity projectiles to experience far greater drag and achieve less than 4× range.",
            formulaBadge = "R ∝ v₀²"
        ),
        PredictInquiry(
            id = "inq_apex_velocity",
            question = "At the highest point (apex) of a projectile's flight, what is its vertical velocity and total acceleration?",
            context = "Recall Newton's second law and vector velocity decomposition in 2D kinematics.",
            options = listOf(
                "v_y = 0 m/s, a = 0 m/s²",
                "v_y = 0 m/s, a = 9.81 m/s² downwards",
                "v_y = v₀ m/s, a = 9.81 m/s² downwards",
                "v_y = 0 m/s, a = 9.81 m/s² upwards"
            ),
            correctIndex = 1,
            deterministicExplanation = "At apex, the vertical motion stops instantaneously before reversing direction, so v_y = 0. However, Earth's gravitational force F = mg acts constantly downward, so the acceleration remains a = g = 9.81 m/s² downward throughout.",
            modelVsRealExplanation = "Mathematical Model vs Reality: Even in turbulent atmospheric flows, gravity acts continuously without pause. If acceleration were zero at apex, the projectile would hover in mid-air indefinitely!",
            formulaBadge = "v_y(t_apex) = 0,   a_y = -g"
        ),
        PredictInquiry(
            id = "inq_optimal_angle",
            question = "In an idealized vacuum, which launch angle θ yields the maximum horizontal range R on level ground?",
            context = "Analyze the trigonometric term sin(2θ) in the range formula.",
            options = listOf(
                "θ = 30°",
                "θ = 45°",
                "θ = 60°",
                "θ = 90°"
            ),
            correctIndex = 1,
            deterministicExplanation = "The range formula has term sin(2θ). The sine function reaches its maximum value of 1.0 when its argument is 90°. Setting 2θ = 90° yields θ = 45°.",
            modelVsRealExplanation = "Mathematical Model vs Reality: On the athletic field or artillery ranges, air drag slows down projectiles, so the optimal angle for maximum range is typically between 38° and 42° rather than 45°.",
            formulaBadge = "d/dθ [sin(2θ)] = 2 cos(2θ) = 0 ⇒ θ = 45°"
        )
    )
}
