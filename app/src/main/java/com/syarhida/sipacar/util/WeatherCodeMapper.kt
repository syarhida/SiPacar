package com.syarhida.sipacar.util

import com.syarhida.sipacar.R

/**
 * Utility class untuk mapping WMO Weather Code ke icon dan kondisi cuaca
 * Berdasarkan WMO Weather interpretation codes
 */
object WeatherCodeMapper {
    
    /**
     * Mendapatkan resource ID icon cuaca berdasarkan weathercode
     * 
     * @param weathercode WMO Weather interpretation code
     * @return Resource ID untuk drawable icon
     */
    fun getWeatherIcon(weathercode: Int): Int {
        return when (weathercode) {
            0 -> R.drawable.ic_weather_clear              // ☀️ Cerah
            1, 2 -> R.drawable.ic_weather_partly_cloudy   // ⛅ Cerah Berawan
            3 -> R.drawable.ic_weather_cloudy             // ☁️ Berawan
            in 45..48 -> R.drawable.ic_weather_fog        // 🌫️ Berkabut
            in 51..57 -> R.drawable.ic_weather_drizzle    // 🌧️ Hujan Ringan
            in 61..67 -> R.drawable.ic_weather_rain       // 🌧️ Hujan
            in 71..77 -> R.drawable.ic_weather_snow       // ❄️ Salju
            in 80..82 -> R.drawable.ic_weather_showers    // 🌦️ Hujan Ringan (showers)
            in 85..86 -> R.drawable.ic_weather_snow       // ❄️ Salju (showers)
            in 95..99 -> R.drawable.ic_weather_thunderstorm // ⛈️ Hujan Petir
            else -> R.drawable.ic_weather_cloudy          // Default: Berawan
        }
    }
    
    /**
     * Mendapatkan kondisi cuaca dalam bahasa Indonesia
     * 
     * @param weathercode WMO Weather interpretation code
     * @return Deskripsi kondisi cuaca
     */
    fun getWeatherCondition(weathercode: Int): String {
        return when (weathercode) {
            0 -> "Cerah"
            1, 2 -> "Cerah Berawan"
            3 -> "Berawan"
            in 45..48 -> "Berkabut"
            in 51..57 -> "Hujan Ringan"
            in 61..67 -> "Hujan"
            in 71..77 -> "Salju"
            in 80..82 -> "Hujan Ringan"
            in 85..86 -> "Salju"
            in 95..99 -> "Hujan Petir"
            else -> "Berawan"
        }
    }
    
    /**
     * Mendapatkan emoji icon cuaca (alternatif jika drawable belum ada)
     * 
     * @param weathercode WMO Weather interpretation code
     * @return Emoji string
     */
    fun getWeatherEmoji(weathercode: Int): String {
        return when (weathercode) {
            0 -> "☀️"           // Cerah
            1, 2 -> "⛅"        // Cerah Berawan
            3 -> "☁️"          // Berawan
            in 45..48 -> "🌫️" // Berkabut
            in 51..57 -> "🌧️" // Hujan Ringan
            in 61..67 -> "🌧️" // Hujan
            in 71..77 -> "❄️"  // Salju
            in 80..82 -> "🌦️" // Hujan Ringan (showers)
            in 85..86 -> "❄️"  // Salju (showers)
            in 95..99 -> "⛈️"  // Hujan Petir
            else -> "☁️"       // Default: Berawan
        }
    }
}

