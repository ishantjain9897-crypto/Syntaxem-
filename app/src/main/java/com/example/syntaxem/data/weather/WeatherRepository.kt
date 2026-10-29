package com.example.syntaxem.data.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

/**
 * Interface defining the Weather Repository capabilities including
 * permission-based location retrieval, mock API fetching, caching, and offline states.
 */
interface IWeatherRepository {
    val currentWeather: StateFlow<WeatherSnapshot?>
    val weatherState: StateFlow<WeatherResult<WeatherSnapshot>>
    val isWeatherEnabled: StateFlow<Boolean>
    val selectedCity: StateFlow<CityLocation>
    val locationPermissionState: StateFlow<LocationPermissionState>

    suspend fun refreshWeather(
        latitude: Double? = null,
        longitude: Double? = null,
        locationLabel: String? = null
    ): WeatherResult<WeatherSnapshot>

    suspend fun refreshWeatherWithLocation(
        hasPermissionOverride: Boolean? = null,
        forceRefresh: Boolean = false
    ): WeatherResult<WeatherSnapshot>

    fun setWeatherEnabled(enabled: Boolean)
    fun setSelectedCity(city: CityLocation)
    fun clearCache()
    fun getApiService(): WeatherApiService
}

/**
 * Production-ready Weather Repository handling:
 * 1. Permission-based location retrieval (ACCESS_COARSE_LOCATION / ACCESS_FINE_LOCATION)
 * 2. Fetching from mock weather API with configurable responses and offline modes
 * 3. SharedPreferences-backed caching with Time-To-Live (TTL) and stale flags
 * 4. Resilient offline-ready error states with cached fallback
 */
class WeatherRepository(
    private val context: Context,
    private val apiService: WeatherApiService = MockWeatherApi()
) : IWeatherRepository {

    private val prefs = context.getSharedPreferences("syntaxem_weather_cache", Context.MODE_PRIVATE)

    private val _currentWeather = MutableStateFlow<WeatherSnapshot?>(loadCachedWeather())
    override val currentWeather: StateFlow<WeatherSnapshot?> = _currentWeather.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherResult<WeatherSnapshot>>(
        _currentWeather.value?.let { WeatherResult.Success(it, isFromCache = true, isStale = it.isStale) }
            ?: WeatherResult.Error(
                message = "Weather data not yet fetched",
                cachedData = null,
                isOffline = false,
                recoverySuggestion = "Pull to refresh or select a city."
            )
    )
    override val weatherState: StateFlow<WeatherResult<WeatherSnapshot>> = _weatherState.asStateFlow()

    private val _isWeatherEnabled = MutableStateFlow(prefs.getBoolean("weather_enabled", true))
    override val isWeatherEnabled: StateFlow<Boolean> = _isWeatherEnabled.asStateFlow()

    private val _selectedCity = MutableStateFlow(
        loadSavedCity()
    )
    override val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _locationPermissionState = MutableStateFlow(checkLocationPermissionInternal())
    override val locationPermissionState: StateFlow<LocationPermissionState> = _locationPermissionState.asStateFlow()

    companion object {
        const val CACHE_TTL_MS = 30 * 60 * 1000L // 30 minutes cache validity

        @Volatile
        private var INSTANCE: WeatherRepository? = null

        fun getInstance(context: Context, apiService: WeatherApiService = MockWeatherApi()): WeatherRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherRepository(context.applicationContext, apiService)
                INSTANCE = instance
                instance
            }
        }

        fun resetInstanceForTesting() {
            INSTANCE = null
        }
    }

    override fun getApiService(): WeatherApiService = apiService

    override fun setWeatherEnabled(enabled: Boolean) {
        _isWeatherEnabled.value = enabled
        prefs.edit().putBoolean("weather_enabled", enabled).apply()
    }

    override fun setSelectedCity(city: CityLocation) {
        _selectedCity.value = city
        prefs.edit()
            .putString("city_name", city.name)
            .putString("city_state", city.state)
            .putFloat("city_lat", city.latitude.toFloat())
            .putFloat("city_lon", city.longitude.toFloat())
            .apply()
    }

    override fun clearCache() {
        prefs.edit().clear().apply()
        _currentWeather.value = null
        _weatherState.value = WeatherResult.Error(
            message = "Cache cleared",
            cachedData = null,
            isOffline = false
        )
    }

    /**
     * Checks whether location permission is granted on the device.
     */
    private fun checkLocationPermissionInternal(): LocationPermissionState {
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return if (coarse || fine) {
            LocationPermissionState.GRANTED
        } else {
            LocationPermissionState.DENIED
        }
    }

    /**
     * Resolves location based on granted permissions or falls back to selected city.
     */
    suspend fun resolveLocation(hasPermissionOverride: Boolean? = null): ResolvedLocation = withContext(Dispatchers.IO) {
        val isGranted = hasPermissionOverride ?: (checkLocationPermissionInternal() == LocationPermissionState.GRANTED)
        _locationPermissionState.value = if (isGranted) LocationPermissionState.GRANTED else LocationPermissionState.DENIED

        if (!isGranted) {
            val fallbackCity = _selectedCity.value
            return@withContext ResolvedLocation(
                latitude = fallbackCity.latitude,
                longitude = fallbackCity.longitude,
                label = fallbackCity.displayName,
                isFromDeviceGps = false
            )
        }

        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val location: Location? = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: locationManager?.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                ?: locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (location != null) {
                var cityName = "Current Location"
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        val locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                        if (!locality.isNullOrBlank()) {
                            cityName = locality
                        }
                    }
                } catch (ignored: Exception) {
                    // Geocoder lookup failed, use fallback label
                }
                ResolvedLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    label = cityName,
                    isFromDeviceGps = true
                )
            } else {
                // Permission granted but device location unavailable -> fallback to selected city
                val fallbackCity = _selectedCity.value
                ResolvedLocation(
                    latitude = fallbackCity.latitude,
                    longitude = fallbackCity.longitude,
                    label = "${fallbackCity.displayName} (Device GPS Idle)",
                    isFromDeviceGps = false
                )
            }
        } catch (e: SecurityException) {
            _locationPermissionState.value = LocationPermissionState.DENIED
            val fallbackCity = _selectedCity.value
            ResolvedLocation(
                latitude = fallbackCity.latitude,
                longitude = fallbackCity.longitude,
                label = fallbackCity.displayName,
                isFromDeviceGps = false
            )
        } catch (e: Exception) {
            val fallbackCity = _selectedCity.value
            ResolvedLocation(
                latitude = fallbackCity.latitude,
                longitude = fallbackCity.longitude,
                label = fallbackCity.displayName,
                isFromDeviceGps = false
            )
        }
    }

    /**
     * Refreshes weather using permission-based location resolution.
     */
    override suspend fun refreshWeatherWithLocation(
        hasPermissionOverride: Boolean?,
        forceRefresh: Boolean
    ): WeatherResult<WeatherSnapshot> {
        if (!_isWeatherEnabled.value) {
            return WeatherResult.Error(
                message = "Weather dashboard is disabled in settings.",
                cachedData = _currentWeather.value,
                isOffline = false,
                recoverySuggestion = "Enable weather in settings to view live telemetry."
            )
        }

        val resolved = resolveLocation(hasPermissionOverride)
        return refreshWeather(
            latitude = resolved.latitude,
            longitude = resolved.longitude,
            locationLabel = resolved.label
        )
    }

    /**
     * Fetches weather data for explicit or default coordinates with caching and offline handling.
     */
    override suspend fun refreshWeather(
        latitude: Double?,
        longitude: Double?,
        locationLabel: String?
    ): WeatherResult<WeatherSnapshot> {
        if (!_isWeatherEnabled.value) {
            val error = WeatherResult.Error(
                message = "Weather dashboard is disabled.",
                cachedData = _currentWeather.value,
                isOffline = false
            )
            _weatherState.value = error
            return error
        }

        val lat = latitude ?: _selectedCity.value.latitude
        val lon = longitude ?: _selectedCity.value.longitude
        val label = locationLabel ?: _selectedCity.value.displayName

        _weatherState.value = WeatherResult.Loading

        return withContext(Dispatchers.IO) {
            try {
                // Fetch from the Mock Weather API Service
                val response = apiService.fetchWeather(lat, lon, label)
                val now = System.currentTimeMillis()

                val freshSnapshot = WeatherSnapshot(
                    temperature = response.temperature,
                    feelsLikeTemperature = response.feelsLikeTemperature,
                    condition = response.condition,
                    humidity = response.humidity,
                    windSpeed = response.windSpeed,
                    observedAt = now,
                    fetchedAt = now,
                    locationLabel = label,
                    sourceProvider = response.sourceProvider,
                    isStale = false,
                    isAvailable = true,
                    errorMessage = null
                )

                // Save to local cache
                saveCachedWeather(freshSnapshot)
                _currentWeather.value = freshSnapshot

                val successResult = WeatherResult.Success(
                    data = freshSnapshot,
                    isFromCache = false,
                    isStale = false
                )
                _weatherState.value = successResult
                successResult

            } catch (e: Exception) {
                // Offline or API failure: perform graceful fallback to cache
                val reason = e.localizedMessage ?: "Network or API connection failed"
                handleFetchFailure(reason, label)
            }
        }
    }

    private fun handleFetchFailure(reason: String, targetLabel: String): WeatherResult<WeatherSnapshot> {
        val cached = _currentWeather.value ?: loadCachedWeather()

        return if (cached != null) {
            // Offline-ready: serve stale cached data with clear diagnostic label
            val staleSnapshot = cached.copy(
                isStale = true,
                errorMessage = "Offline: $reason"
            )
            _currentWeather.value = staleSnapshot
            val staleResult = WeatherResult.Success(
                data = staleSnapshot,
                isFromCache = true,
                isStale = true
            )
            _weatherState.value = staleResult
            staleResult
        } else {
            // Completely offline and no cache available
            val offlineSnapshot = WeatherSnapshot(
                temperature = 0.0,
                feelsLikeTemperature = 0.0,
                condition = "Offline",
                humidity = 0,
                windSpeed = 0.0,
                observedAt = 0L,
                fetchedAt = 0L,
                locationLabel = targetLabel,
                sourceProvider = "Mock Weather Service (Offline)",
                isStale = true,
                isAvailable = false,
                errorMessage = "Offline: $reason"
            )
            _currentWeather.value = offlineSnapshot
            val errorResult = WeatherResult.Error(
                message = "Weather unavailable: $reason",
                cachedData = null,
                isOffline = true,
                recoverySuggestion = "Device is offline. Connect to internet or select a city manually."
            )
            _weatherState.value = errorResult
            errorResult
        }
    }

    private fun saveCachedWeather(snapshot: WeatherSnapshot) {
        prefs.edit()
            .putFloat("cache_temp", snapshot.temperature.toFloat())
            .putFloat("cache_feels_like", snapshot.feelsLikeTemperature.toFloat())
            .putString("cache_condition", snapshot.condition)
            .putInt("cache_humidity", snapshot.humidity)
            .putFloat("cache_wind", snapshot.windSpeed.toFloat())
            .putLong("cache_observed_at", snapshot.observedAt)
            .putLong("cache_fetched_at", snapshot.fetchedAt)
            .putString("cache_label", snapshot.locationLabel)
            .putString("cache_provider", snapshot.sourceProvider)
            .apply()
    }

    private fun loadCachedWeather(): WeatherSnapshot? {
        if (!prefs.contains("cache_temp")) return null
        val fetchedAt = prefs.getLong("cache_fetched_at", 0L)
        val isStale = (System.currentTimeMillis() - fetchedAt) > CACHE_TTL_MS

        return WeatherSnapshot(
            temperature = prefs.getFloat("cache_temp", 25.0f).toDouble(),
            feelsLikeTemperature = prefs.getFloat("cache_feels_like", 26.0f).toDouble(),
            condition = prefs.getString("cache_condition", "Partly Cloudy") ?: "Partly Cloudy",
            humidity = prefs.getInt("cache_humidity", 55),
            windSpeed = prefs.getFloat("cache_wind", 10.0f).toDouble(),
            observedAt = prefs.getLong("cache_observed_at", 0L),
            fetchedAt = fetchedAt,
            locationLabel = prefs.getString("cache_label", "New Delhi, Delhi NCR") ?: "New Delhi, Delhi NCR",
            sourceProvider = prefs.getString("cache_provider", "Syntaxem Mock Weather (Cached)") ?: "Syntaxem Mock Weather (Cached)",
            isStale = isStale,
            isAvailable = true
        )
    }

    private fun loadSavedCity(): CityLocation {
        val name = prefs.getString("city_name", null)
        val state = prefs.getString("city_state", null)
        val lat = prefs.getFloat("city_lat", Float.NaN)
        val lon = prefs.getFloat("city_lon", Float.NaN)

        return if (name != null && state != null && !lat.isNaN() && !lon.isNaN()) {
            CityLocation(name, state, lat.toDouble(), lon.toDouble())
        } else {
            CityLocation.DEFAULT_CITIES.first()
        }
    }
}
