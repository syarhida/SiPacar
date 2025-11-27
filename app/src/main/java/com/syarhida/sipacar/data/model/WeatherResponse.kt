package com.syarhida.sipacar.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model untuk response dari API Open-Meteo
 * Berisi data prakiraan cuaca per jam
 */
data class WeatherResponse(
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double,
    
    @SerializedName("hourly")
    val hourly: HourlyData
)

/**
 * Data cuaca per jam
 */
data class HourlyData(
    @SerializedName("time")
    val time: List<String>, // Format: "2024-01-01T00:00"
    
    @SerializedName("temperature_2m")
    val temperature: List<Double>, // Suhu dalam Celsius
    
    @SerializedName("relative_humidity_2m")
    val humidity: List<Int> // Kelembapan dalam persen
)

