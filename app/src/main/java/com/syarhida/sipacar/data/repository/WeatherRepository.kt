package com.syarhida.sipacar.data.repository

import com.syarhida.sipacar.data.api.RetrofitInstance
import com.syarhida.sipacar.data.model.DailyWeatherCard
import com.syarhida.sipacar.data.model.HourlyWeatherItem
import com.syarhida.sipacar.data.model.WeatherIconType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository untuk mengelola data cuaca
 * Bertanggung jawab untuk mengambil data dari API dan memproses data
 */
class WeatherRepository {
    
    private val api = RetrofitInstance.weatherApi
    
    /**
     * Mengambil data card cuaca harian (4 hari ke depan)
     */
    suspend fun getDailyWeatherCards(): Result<List<DailyWeatherCard>> {
        return try {
            val response = api.getWeatherForecast()
            
            if (response.isSuccessful && response.body() != null) {
                val weatherResponse = response.body()!!
                val cards = mutableListOf<DailyWeatherCard>()
                
                // Group data by date
                val groupedByDate = weatherResponse.hourly.time.indices.groupBy { index ->
                    weatherResponse.hourly.time[index].substring(0, 10) // "2024-11-27"
                }
                
                // Ambil 4 hari pertama
                val dates = groupedByDate.keys.take(4)
                val today = getCurrentDate()
                
                dates.forEachIndexed { index, dateString ->
                    val indices = groupedByDate[dateString] ?: emptyList()
                    if (indices.isNotEmpty()) {
                        // Rata-rata suhu untuk hari tersebut
                        val avgTemp = indices.map { weatherResponse.hourly.temperature[it] }.average()
                        val avgHumidity = indices.map { weatherResponse.hourly.humidity[it] }.average()
                        
                        // Tentukan icon berdasarkan jam 12:00 (siang)
                        val noonIndex = indices.find { 
                            weatherResponse.hourly.time[it].contains("T12:00")
                        } ?: indices[indices.size / 2]
                        val iconType = getIconTypeFromTime(weatherResponse.hourly.time[noonIndex])
                        
                        val isToday = dateString == today
                        val dateLabel = when {
                            isToday -> "Hari Ini"
                            index == 1 -> "Besok"
                            else -> formatDateToIndonesian(dateString)
                        }
                        
                        val dayName = getDayName(dateString)
                        
                        cards.add(
                            DailyWeatherCard(
                                date = dateLabel,
                                dayName = dayName,
                                temperature = "${avgTemp.toInt()}°",
                                humidity = "${avgHumidity.toInt()}%",
                                iconType = iconType,
                                isToday = isToday
                            )
                        )
                    }
                }
                
                Result.success(cards)
            } else {
                Result.failure(Exception("Gagal memuat data cuaca"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Mengambil data cuaca per jam
     * Untuk hari ini: dari jam sekarang sampai 23:00
     * Untuk hari lain: dari 00:00 sampai 23:00
     */
    suspend fun getHourlyWeatherItems(): Result<List<HourlyWeatherItem>> {
        return try {
            val response = api.getWeatherForecast()
            
            if (response.isSuccessful && response.body() != null) {
                val weatherResponse = response.body()!!
                val items = mutableListOf<HourlyWeatherItem>()
                
                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val currentDate = getCurrentDate()
                
                weatherResponse.hourly.time.forEachIndexed { index, timeString ->
                    val date = timeString.substring(0, 10)
                    val hour = timeString.substring(11, 13).toInt()
                    
                    // Filter: untuk hari ini (jam sekarang sampai 23:00) atau hari lain (00:00-23:00)
                    val shouldInclude = if (date == currentDate) {
                        hour >= currentHour // Hari ini: dari jam sekarang
                    } else {
                        true // Hari lain: semua jam
                    }
                    
                    if (shouldInclude) {
                        val temp = weatherResponse.hourly.temperature[index]
                        val humidity = weatherResponse.hourly.humidity[index]
                        val iconType = getIconTypeFromTime(timeString)
                        
                        items.add(
                            HourlyWeatherItem(
                                time = formatTimeToWIB(timeString),
                                temperature = "${temp.toInt()}°C",
                                weatherDesc = getWeatherDescription(iconType),
                                humidity = "${humidity}%",
                                iconType = iconType
                            )
                        )
                    }
                }
                
                Result.success(items)
            } else {
                Result.failure(Exception("Gagal memuat data cuaca"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get current date in format YYYY-MM-DD
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
    
    /**
     * Format waktu ke format WIB (e.g., "12.00 WIB")
     */
    private fun formatTimeToWIB(isoTime: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val date = inputFormat.parse(isoTime) ?: return isoTime
            
            val timeFormat = SimpleDateFormat("HH.mm", Locale.getDefault())
            return "${timeFormat.format(date)} WIB"
        } catch (e: Exception) {
            return isoTime
        }
    }
    
    /**
     * Format tanggal ke bahasa Indonesia (e.g., "29 November")
     */
    private fun formatDateToIndonesian(dateString: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return dateString
            
            val calendar = Calendar.getInstance()
            calendar.time = date
            
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = when (calendar.get(Calendar.MONTH)) {
                Calendar.JANUARY -> "Januari"
                Calendar.FEBRUARY -> "Februari"
                Calendar.MARCH -> "Maret"
                Calendar.APRIL -> "April"
                Calendar.MAY -> "Mei"
                Calendar.JUNE -> "Juni"
                Calendar.JULY -> "Juli"
                Calendar.AUGUST -> "Agustus"
                Calendar.SEPTEMBER -> "September"
                Calendar.OCTOBER -> "Oktober"
                Calendar.NOVEMBER -> "November"
                Calendar.DECEMBER -> "Desember"
                else -> ""
            }
            
            return "$day $month"
        } catch (e: Exception) {
            return dateString
        }
    }
    
    /**
     * Get nama hari dalam bahasa Indonesia
     */
    private fun getDayName(dateString: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return ""
            
            val calendar = Calendar.getInstance()
            calendar.time = date
            
            return when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Minggu"
                Calendar.MONDAY -> "Senin"
                Calendar.TUESDAY -> "Selasa"
                Calendar.WEDNESDAY -> "Rabu"
                Calendar.THURSDAY -> "Kamis"
                Calendar.FRIDAY -> "Jumat"
                Calendar.SATURDAY -> "Sabtu"
                else -> ""
            }
        } catch (e: Exception) {
            return ""
        }
    }
    
    /**
     * Get deskripsi cuaca berdasarkan icon type
     */
    private fun getWeatherDescription(iconType: WeatherIconType): String {
        return when (iconType) {
            WeatherIconType.PAGI -> "Cerah Pagi"
            WeatherIconType.SIANG -> "Berawan"
            WeatherIconType.SORE -> "Sore"
            WeatherIconType.MALAM -> "Malam"
        }
    }
    
    /**
     * Menentukan tipe icon cuaca berdasarkan jam
     */
    private fun getIconTypeFromTime(isoTime: String): WeatherIconType {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val date = inputFormat.parse(isoTime) ?: return WeatherIconType.SIANG
            
            val calendar = Calendar.getInstance()
            calendar.time = date
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            
            return when (hour) {
                in 5..10 -> WeatherIconType.PAGI    // 05:00 - 10:59
                in 11..14 -> WeatherIconType.SIANG  // 11:00 - 14:59
                in 15..17 -> WeatherIconType.SORE   // 15:00 - 17:59
                else -> WeatherIconType.MALAM        // 18:00 - 04:59
            }
        } catch (e: Exception) {
            return WeatherIconType.SIANG
        }
    }
}
