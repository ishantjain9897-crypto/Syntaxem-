package com.example.orbitlab.model

import kotlin.math.*

/**
 * Predefined celestial bodies for gravitational acceleration.
 */
enum class CelestialBody(val displayName: String, val gravity: Double, val badgeText: String) {
    EARTH("Earth", 9.80665, "1.00g (9.81 m/s²)"),
    MOON("Moon", 1.62, "0.17g (1.62 m/s²)"),
    MARS("Mars", 3.72, "0.38g (3.71 m/s²)"),
    JUPITER("Jupiter", 24.79, "2.53g (24.79 m/s²)"),
    CUSTOM("Custom Field", 9.80665, "Variable")
}

/**
 * Deterministic flight parameters for projectile motion.
 */
data class FlightParameters(
    val initialVelocity: Double = 25.0, // m/s, range [1.0, 100.0]
    val launchAngleDegrees: Double = 45.0, // deg, range [0.0, 90.0]
    val gravity: Double = 9.80665, // m/s^2, range [0.5, 30.0]
    val celestialBody: CelestialBody = CelestialBody.EARTH,
    val initialHeight: Double = 0.0 // m
) {
    val launchAngleRadians: Double get() = Math.toRadians(launchAngleDegrees)
    val v0x: Double get() = initialVelocity * cos(launchAngleRadians)
    val v0y: Double get() = initialVelocity * sin(launchAngleRadians)

    /**
     * Deterministic time of flight:
     * When y = y0 + v0y*t - 0.5*g*t^2 = 0
     * t = (v0y + sqrt(v0y^2 + 2*g*y0)) / g
     */
    val timeOfFlight: Double
        get() {
            if (gravity <= 0.001) return 0.0
            val discriminant = v0y * v0y + 2.0 * gravity * initialHeight
            return if (discriminant >= 0) {
                (v0y + sqrt(discriminant)) / gravity
            } else {
                0.0
            }
        }

    /**
     * Maximum height reached:
     * H_max = y0 + (v0y^2) / (2*g)
     */
    val maxHeight: Double
        get() {
            if (gravity <= 0.001) return initialHeight
            return initialHeight + (v0y * v0y) / (2.0 * gravity)
        }

    /**
     * Total horizontal range:
     * R = v0x * timeOfFlight
     */
    val horizontalRange: Double
        get() = v0x * timeOfFlight

    /**
     * Time at maximum height:
     * t_apex = v0y / g
     */
    val timeToApex: Double
        get() = if (gravity > 0.001) (v0y / gravity).coerceAtLeast(0.0) else 0.0

    /**
     * Compute state at time t:
     * x(t) = v0x * t
     * y(t) = y0 + v0y * t - 0.5 * g * t^2
     * vx(t) = v0x
     * vy(t) = v0y - g * t
     */
    fun computeState(time: Double): TrajectoryPoint {
        val t = time.coerceIn(0.0, timeOfFlight)
        val x = v0x * t
        val y = (initialHeight + v0y * t - 0.5 * gravity * t * t).coerceAtLeast(0.0)
        val vx = v0x
        val vy = v0y - gravity * t
        val speed = sqrt(vx * vx + vy * vy)
        return TrajectoryPoint(
            time = t,
            x = x,
            y = y,
            vx = vx,
            vy = vy,
            speed = speed
        )
    }

    /**
     * Generate dense discrete sample points for smooth rendering of trajectory curve.
     */
    fun generateTrajectoryCurve(samples: Int = 120): List<TrajectoryPoint> {
        val totalTime = timeOfFlight
        if (totalTime <= 0.001) return listOf(computeState(0.0))
        return (0..samples).map { i ->
            val t = (i.toDouble() / samples) * totalTime
            computeState(t)
        }
    }
}

/**
 * State of projectile at instant t.
 */
data class TrajectoryPoint(
    val time: Double,
    val x: Double,
    val y: Double,
    val vx: Double,
    val vy: Double,
    val speed: Double
)

/**
 * Saved experimental run for side-by-side comparison and replay.
 */
data class SavedRun(
    val id: String,
    val label: String,
    val params: FlightParameters,
    val timestamp: Long = System.currentTimeMillis()
)
