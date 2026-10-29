package com.example.syntaxem.data.weather

import kotlinx.coroutines.delay
import java.io.IOException
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.sin

/**
 * Data model returned by the mock weather API.
 */
data class MockWeatherApiResponse(
    val temperature: Double,
    val feelsLikeTemperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val locationLabel: String,
    val sourceProvider: String = "Syntaxem Mock Weather Service (Simulated Academic Environment)"
)

/**
 * Interface representing weather API data provider.
 */
interface WeatherApiService {
    suspend fun fetchWeather(latitude: Double, longitude: Double, locationLabel: String): MockWeatherApiResponse
}

/**
 * Mock Weather API implementation simulating realistic academic environment telemetry.
 * Supports configurable simulation of network delay and offline/error states for testability.
 */
class MockWeatherApi(
    private var simulatedDelayMs: Long = 80L,
    private var forceError: Boolean = false,
    private var simulatedErrorMessage: String = "Network unreachable (Offline mode)"
) : WeatherApiService {

    fun setSimulateError(shouldError: Boolean, message: String = "Network unreachable (Offline mode)") {
        this.forceError = shouldError
        this.simulatedErrorMessage = message
    }

    fun setSimulatedDelay(delayMs: Long) {
        this.simulatedDelayMs = delayMs
    }

    override suspend fun fetchWeather(
        latitude: Double,
        longitude: Double,
        locationLabel: String
    ): MockWeatherApiResponse {
        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }

        if (forceError) {
            throw IOException(simulatedErrorMessage)
        }

        // Generate realistic, consistent atmospheric metrics based on coordinates and current hour
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        // Diurnal temperature curve peaking around 14:00 (2 PM)
        val diurnalFactor = sin((hour - 8) * Math.PI / 12.0).coerceIn(-1.0, 1.0)
        
        // Base temperature derived from latitude (equatorial vs northern regions)
        val baseTemp = (32.0 - (abs(latitude - 20.0) * 0.4)).coerceIn(16.0, 36.0)
        val temp = Math.round((baseTemp + (diurnalFactor * 4.5)) * 10.0) / 10.0
        val feelsLike = Math.round((temp + (diurnalFactor * 1.5) + 1.2) * 10.0) / 10.0

        // Humidity inversely relates to temperature peak
        val humidity = ((60 - (diurnalFactor * 15)).toInt() + (abs(longitude.toInt()) % 10)).coerceIn(30, 85)
        val windSpeed = Math.round((8.0 + (abs(latitude.toInt() % 7) * 1.2)) * 10.0) / 10.0

        // Realistic conditions based on coordinate hash and time
        val (condition, code) = determineCondition(latitude, longitude, hour)

        return MockWeatherApiResponse(
            temperature = temp,
            feelsLikeTemperature = feelsLike,
            condition = condition,
            humidity = humidity,
            windSpeed = windSpeed,
            weatherCode = code,
            locationLabel = locationLabel
        )
    }

    private fun determineCondition(lat: Double, lon: Double, hour: Int): Pair<String, Int> {
        val seed = (abs(lat * 100).toInt() + abs(lon * 100).toInt() + hour) % 10
        return when (seed) {
            0, 1, 2, 3 -> "Clear Sky" to 0
            4, 5 -> "Mainly Clear" to 1
            6, 7 -> "Partly Cloudy" to 2
            8 -> "Fog & Haze" to 45
            else -> "Light Drizzle" to 51
        }
    }
}
