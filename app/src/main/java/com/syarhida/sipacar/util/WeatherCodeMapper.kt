package com.syarhida.sipacar.util

/**
 * Utility class untuk mapping WMO Weather Code ke emoji icon dan kondisi cuaca
 * Berdasarkan WMO Weather interpretation codes
 */
object WeatherCodeMapper {
    
    /**
     * Mendapatkan emoji icon cuaca berdasarkan weathercode
     * 
     * @param weathercode WMO Weather interpretation code
     * @return Emoji string
     */
    fun getWeatherIcon(weathercode: Int): String {
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
    
}

