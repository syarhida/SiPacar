package com.syarhida.sipacar.data.repository

import com.syarhida.sipacar.data.api.RetrofitInstance
import com.syarhida.sipacar.data.model.WeatherItem
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
     * Mengambil data prakiraan cuaca dari API
     * dan mengkonversi ke format yang siap ditampilkan
     * 
     * @return Result berisi list WeatherItem atau error message
     */
    suspend fun getWeatherForecast(): Result<List<WeatherItem>> {
        return try {
            val response = api.getWeatherForecast()
            
            if (response.isSuccessful && response.body() != null) {
                val weatherResponse = response.body()!!
                val weatherItems = mutableListOf<WeatherItem>()
                
                // Ambil data 24 jam ke depan saja (atau semua data yang tersedia)
                val timeList = weatherResponse.hourly.time
                val tempList = weatherResponse.hourly.temperature
                
                // Batasi hingga 24 item atau sesuai data yang tersedia
                val itemCount = minOf(24, timeList.size)
                
                for (i in 0 until itemCount) {
                    val time = timeList[i]
                    val temp = tempList[i]
                    
                    // Konversi waktu ke format Indonesia
                    val formattedTime = formatTimeToIndonesian(time)
                    
                    // Format suhu
                    val formattedTemp = "${temp.toInt()}°C"
                    
                    // Tentukan tipe icon berdasarkan jam
                    val iconType = getIconTypeFromTime(time)
                    
                    weatherItems.add(
                        WeatherItem(
                            time = formattedTime,
                            temperature = formattedTemp,
                            iconType = iconType
                        )
                    )
                }
                
                Result.success(weatherItems)
            } else {
                Result.failure(Exception("Gagal memuat data cuaca"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Konversi waktu dari format ISO 8601 ke format Indonesia
     * Contoh: "2024-01-11T14:00" -> "Kamis, 14:00"
     */
    private fun formatTimeToIndonesian(isoTime: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val date = inputFormat.parse(isoTime) ?: return isoTime
            
            // Format hari dalam bahasa Indonesia
            val calendar = Calendar.getInstance()
            calendar.time = date
            
            val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Minggu"
                Calendar.MONDAY -> "Senin"
                Calendar.TUESDAY -> "Selasa"
                Calendar.WEDNESDAY -> "Rabu"
                Calendar.THURSDAY -> "Kamis"
                Calendar.FRIDAY -> "Jumat"
                Calendar.SATURDAY -> "Sabtu"
                else -> ""
            }
            
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeString = timeFormat.format(date)
            
            return "$dayOfWeek, $timeString"
        } catch (e: Exception) {
            return isoTime
        }
    }
    
    /**
     * Menentukan tipe icon cuaca berdasarkan jam
     * 
     * @param isoTime Waktu dalam format ISO 8601
     * @return WeatherIconType yang sesuai
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

