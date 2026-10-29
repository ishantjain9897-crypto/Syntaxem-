package com.example.syntaxem.data.weather

/**
 * Standardized snapshot of local environmental weather data.
 */
data class WeatherSnapshot(
    val temperature: Double,
    val feelsLikeTemperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val observedAt: Long,
    val fetchedAt: Long,
    val locationLabel: String,
    val sourceProvider: String = "Open-Meteo / WMO Standard Meteorological Service",
    val isStale: Boolean = false,
    val isAvailable: Boolean = true,
    val errorMessage: String? = null
) {
    val freshnessLabel: String
        get() {
            if (!isAvailable) return "Weather Unavailable"
            val diffMinutes = ((System.currentTimeMillis() - fetchedAt) / (1000 * 60)).coerceAtLeast(0)
            return when {
                isStale -> "Cached ($diffMinutes min ago • Offline)"
                diffMinutes < 1 -> "Live • Just updated"
                diffMinutes < 60 -> "Updated ${diffMinutes}m ago"
                else -> "Cached (${diffMinutes / 60}h ago)"
            }
        }

    val conditionIcon: String
        get() = when {
            condition.contains("Rain", ignoreCase = true) -> "rainy"
            condition.contains("Cloud", ignoreCase = true) -> "cloud"
            condition.contains("Snow", ignoreCase = true) -> "ac_unit"
            condition.contains("Thunder", ignoreCase = true) -> "thunderstorm"
            condition.contains("Haze", ignoreCase = true) || condition.contains("Fog", ignoreCase = true) -> "foggy"
            else -> "wb_sunny"
        }
}

/**
 * Approximate geographical coordinates for city-level weather without precise GPS tracking.
 */
data class CityLocation(
    val name: String,
    val state: String,
    val latitude: Double,
    val longitude: Double
) {
    val displayName: String get() = "$name, $state"

    companion object {
        val DEFAULT_CITIES = listOf(
            CityLocation("New Delhi", "Delhi NCR", 28.6139, 77.2090),
            CityLocation("Prayagraj", "Uttar Pradesh", 25.4358, 81.8463),
            CityLocation("Varanasi", "Uttar Pradesh", 25.3176, 82.9739),
            CityLocation("Jaipur", "Rajasthan", 26.9124, 75.7873),
            CityLocation("Lucknow", "Uttar Pradesh", 26.8467, 80.9462),
            CityLocation("Patna", "Bihar", 25.5941, 85.1376),
            CityLocation("Bhopal", "Madhya Pradesh", 23.2599, 77.4126),
            CityLocation("Bengaluru", "Karnataka", 12.9716, 77.5946),
            CityLocation("Mumbai", "Maharashtra", 19.0760, 72.8777),
            CityLocation("Kolkata", "West Bengal", 22.5726, 88.3639)
        )
    }
}
