# ============================================================================
# Syntaxem Production ProGuard / R8 Rules for Release & App Store Distribution
# ============================================================================

# Retain source debugging line numbers for production crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Strip all android.util.Log invocations in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# ----------------------------------------------------------------------------
# Kotlin Coroutines & Flow
# ----------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ----------------------------------------------------------------------------
# Jetpack Compose & Material 3
# ----------------------------------------------------------------------------
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
-keep class androidx.compose.material3.** { *; }

# ----------------------------------------------------------------------------
# Room Database & SQLite
# ----------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class com.example.syntaxem.data.local.entity.** { *; }
-keep class com.example.syntaxem.data.local.dao.** { *; }
-keep class com.example.core.database.** { *; }

# ----------------------------------------------------------------------------
# Retrofit, OkHttp & Moshi (Weather & API Telemetry)
# ----------------------------------------------------------------------------
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep class retrofit2.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Moshi serialization / JSON Data Transfer Objects
-keep class com.squareup.moshi.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class com.example.syntaxem.data.weather.** { *; }
-keep class com.example.syntaxem.model.** { *; }

# ----------------------------------------------------------------------------
# Google Play Services & Location APIs
# ----------------------------------------------------------------------------
-keep class com.google.android.gms.location.** { *; }
-dontwarn com.google.android.gms.**

# ----------------------------------------------------------------------------
# Firebase AI Logic & App Check
# ----------------------------------------------------------------------------
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keepattributes *Annotation*, Signature, EnclosingMethod, InnerClasses
