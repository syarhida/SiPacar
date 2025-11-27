package com.syarhida.sipacar.data.model

/**
 * Model untuk card cuaca harian (daily forecast)
 */
data class DailyWeatherCard(
    val date: String,              // e.g., "Hari Ini", "Besok", "29 November"
    val dayName: String,           // e.g., "Kamis", "Jumat"
    val temperature: String,       // e.g., "28°"
    val humidity: String,          // e.g., "74%"
    val iconType: WeatherIconType, // Icon cuaca
    val isToday: Boolean = false   // Flag untuk styling hari ini
)

/**
 * Model untuk item cuaca per jam
 */
data class HourlyWeatherItem(
    val time: String,              // e.g., "12.00 WIB"
    val temperature: String,       // e.g., "32°C"
    val weatherDesc: String,       // e.g., "Berawan"
    val humidity: String,          // e.g., "56%"
    val iconType: WeatherIconType  // Icon cuaca
)

/**
 * Enum untuk tipe icon cuaca berdasarkan waktu
 */
enum class WeatherIconType {
    PAGI,    // 05:00 - 10:59 (sunrise/morning)
    SIANG,   // 11:00 - 14:59 (day/afternoon)
    SORE,    // 15:00 - 17:59 (evening)
    MALAM    // 18:00 - 04:59 (night)
}

