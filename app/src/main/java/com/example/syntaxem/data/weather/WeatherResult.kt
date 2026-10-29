package com.example.syntaxem.data.weather

/**
 * Sealed result type representing the outcome of weather operations,
 * supporting offline-ready states and cached fallbacks.
 */
sealed class WeatherResult<out T> {
    data class Success<T>(
        val data: T,
        val isFromCache: Boolean = false,
        val isStale: Boolean = false
    ) : WeatherResult<T>()

    data class Error(
        val message: String,
        val cachedData: WeatherSnapshot? = null,
        val isOffline: Boolean = true,
        val recoverySuggestion: String = "Check internet connection or select a city manually."
    ) : WeatherResult<Nothing>()

    object Loading : WeatherResult<Nothing>()
}

/**
 * Encapsulates the location resolution state for permission-based weather.
 */
enum class LocationPermissionState {
    GRANTED,
    DENIED,
    UNAVAILABLE
}

data class ResolvedLocation(
    val latitude: Double,
    val longitude: Double,
    val label: String,
    val isFromDeviceGps: Boolean
)
