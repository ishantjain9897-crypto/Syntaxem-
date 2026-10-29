package com.example

import com.example.orbitlab.model.CelestialBody
import com.example.orbitlab.model.FlightParameters
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class OrbitLabPhysicsUnitTest {

    @Test
    fun testComplementaryAnglesProduceEqualHorizontalRange() {
        // Class 11 Physics Theorem: Angles theta and 90 - theta produce identical range in vacuum
        val v0 = 25.0
        val g = 9.80665

        val run30 = FlightParameters(
            initialVelocity = v0,
            launchAngleDegrees = 30.0,
            gravity = g
        )
        val run60 = FlightParameters(
            initialVelocity = v0,
            launchAngleDegrees = 60.0,
            gravity = g
        )

        // R = v0^2 * sin(2*30) / g = v0^2 * sin(60) / g
        // R = v0^2 * sin(2*60) / g = v0^2 * sin(120) / g = v0^2 * sin(60) / g
        assertTrue(abs(run30.horizontalRange - run60.horizontalRange) < 1e-4)

        // Higher angle spends longer in flight and reaches greater apex
        assertTrue(run60.maxHeight > run30.maxHeight)
        assertTrue(run60.timeOfFlight > run30.timeOfFlight)
    }

    @Test
    fun testMaximumRangeAt45Degrees() {
        val v0 = 30.0
        val g = 9.80665

        val run45 = FlightParameters(initialVelocity = v0, launchAngleDegrees = 45.0, gravity = g)
        val run40 = FlightParameters(initialVelocity = v0, launchAngleDegrees = 40.0, gravity = g)
        val run50 = FlightParameters(initialVelocity = v0, launchAngleDegrees = 50.0, gravity = g)

        assertTrue(run45.horizontalRange > run40.horizontalRange)
        assertTrue(run45.horizontalRange > run50.horizontalRange)
    }

    @Test
    fun testApexVerticalVelocityIsZero() {
        val params = FlightParameters(
            initialVelocity = 40.0,
            launchAngleDegrees = 45.0,
            gravity = 9.80665
        )

        val apexState = params.computeState(params.timeToApex)
        // Vertical velocity component vy should be 0 at apex
        assertTrue("Vertical velocity at apex must be approximately 0", abs(apexState.vy) < 1e-3)
        // Horizontal velocity vx remains v0 * cos(45)
        val expectedVx = 40.0 * kotlin.math.cos(Math.toRadians(45.0))
        assertTrue(abs(apexState.vx - expectedVx) < 1e-4)
    }

    @Test
    fun testLunarGravityMultipliesRangeSixFold() {
        val v0 = 20.0
        val angle = 45.0

        val earthRun = FlightParameters(v0, angle, CelestialBody.EARTH.gravity, CelestialBody.EARTH)
        val moonRun = FlightParameters(v0, angle, CelestialBody.MOON.gravity, CelestialBody.MOON)

        val ratio = moonRun.horizontalRange / earthRun.horizontalRange
        val expectedRatio = CelestialBody.EARTH.gravity / CelestialBody.MOON.gravity

        assertTrue(abs(ratio - expectedRatio) < 1e-3)
        // Lunar range is approx 6x Earth range
        assertTrue(ratio > 5.9 && ratio < 6.2)
    }
}
