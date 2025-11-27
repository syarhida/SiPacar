package com.syarhida.sipacar.data.model

/**
 * Model untuk item cuaca yang ditampilkan di RecyclerView
 */
data class WeatherItem(
    val time: String,          // Waktu dalam format Indonesia (e.g., "Kamis, 14:00")
    val temperature: String,   // Suhu dalam Celsius (e.g., "28°C")
    val iconType: WeatherIconType // Tipe icon berdasarkan waktu
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

