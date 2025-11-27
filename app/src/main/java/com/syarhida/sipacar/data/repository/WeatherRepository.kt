package com.syarhida.sipacar.data.repository

import com.syarhida.sipacar.data.api.RetrofitInstance
import com.syarhida.sipacar.data.model.DailyWeatherCard
import com.syarhida.sipacar.data.model.HourlyWeatherItem
import com.syarhida.sipacar.data.model.WeatherIconType
import com.syarhida.sipacar.util.WeatherCodeMapper
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
    suspend fun getDailyWeatherCards(selectedDate: String? = null): Result<List<DailyWeatherCard>> {
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
                        
                        // Tentukan weathercode berdasarkan jam 12:00 (siang)
                        val noonIndex = indices.find { 
                            weatherResponse.hourly.time[it].contains("T12:00")
                        } ?: indices[indices.size / 2]
                        
                        val weathercode = weatherResponse.hourly.weathercode[noonIndex]
                        val iconType = getIconTypeFromWeatherCode(weathercode)
                        
                        val isToday = dateString == today
                        val isSelected = dateString == (selectedDate ?: today)
                        
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
                                dateString = dateString,
                                temperature = "${avgTemp.toInt()}°",
                                humidity = "${avgHumidity.toInt()}%",
                                weathercode = weathercode,
                                iconType = iconType,
                                isToday = isToday,
                                isSelected = isSelected
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
     * Mengambil data cuaca per jam untuk tanggal tertentu
     * 
     * @param targetDate Tanggal target (format "2024-11-27"), null untuk hari ini
     */
    suspend fun getHourlyWeatherItems(targetDate: String? = null): Result<List<HourlyWeatherItem>> {
        return try {
            val response = api.getWeatherForecast()
            
            if (response.isSuccessful && response.body() != null) {
                val weatherResponse = response.body()!!
                val items = mutableListOf<HourlyWeatherItem>()
                
                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val currentDate = getCurrentDate()
                val dateToShow = targetDate ?: currentDate
                
                weatherResponse.hourly.time.forEachIndexed { index, timeString ->
                    val date = timeString.substring(0, 10)
                    val hour = timeString.substring(11, 13).toInt()
                    
                    // Filter berdasarkan tanggal yang dipilih
                    if (date == dateToShow) {
                        // Jika hari ini (baik default maupun dipilih), filter dari jam sekarang
                        // Jika hari lain, tampilkan semua jam (00:00-23:00)
                        val shouldInclude = if (date == currentDate) {
                            hour >= currentHour // Hari ini: dari jam sekarang
                        } else {
                            true // Hari lain: semua jam (00:00-23:00)
                        }
                        
                        if (shouldInclude) {
                            val temp = weatherResponse.hourly.temperature[index]
                            val humidity = weatherResponse.hourly.humidity[index]
                            val weathercode = weatherResponse.hourly.weathercode[index]
                            
                            val iconType = getIconTypeFromWeatherCode(weathercode)
                            val weatherDesc = WeatherCodeMapper.getWeatherCondition(weathercode)
                            
                            items.add(
                                HourlyWeatherItem(
                                    time = formatTimeToWIB(timeString),
                                    temperature = "${temp.toInt()}°C",
                                    weatherDesc = weatherDesc,
                                    humidity = "${humidity}%",
                                    weathercode = weathercode,
                                    iconType = iconType
                                )
                            )
                        }
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
     * Mengambil data cuaca saat ini (jam sekarang)
     */
    suspend fun getCurrentWeather(): Result<HourlyWeatherItem?> {
        return try {
            val response = api.getWeatherForecast()
            
            if (response.isSuccessful && response.body() != null) {
                val weatherResponse = response.body()!!
                val currentDate = getCurrentDate()
                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                
                // Cari data untuk hari ini dan jam sekarang
                weatherResponse.hourly.time.forEachIndexed { index, timeString ->
                    val date = timeString.substring(0, 10)
                    val hour = timeString.substring(11, 13).toInt()
                    
                    if (date == currentDate && hour == currentHour) {
                        val temp = weatherResponse.hourly.temperature[index]
                        val humidity = weatherResponse.hourly.humidity[index]
                        val weathercode = weatherResponse.hourly.weathercode[index]
                        
                        val iconType = getIconTypeFromWeatherCode(weathercode)
                        val weatherDesc = WeatherCodeMapper.getWeatherCondition(weathercode)
                        
                        return Result.success(
                            HourlyWeatherItem(
                                time = formatTimeToWIB(timeString),
                                temperature = "${temp.toInt()}°C",
                                weatherDesc = weatherDesc,
                                humidity = "${humidity}%",
                                weathercode = weathercode,
                                iconType = iconType
                            )
                        )
                    }
                }
                
                Result.success(null)
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
                Calendar.JANUARY -> "Jan"
                Calendar.FEBRUARY -> "Feb"
                Calendar.MARCH -> "Mar"
                Calendar.APRIL -> "Apr"
                Calendar.MAY -> "Mei"
                Calendar.JUNE -> "Jun"
                Calendar.JULY -> "Jul"
                Calendar.AUGUST -> "Agt"
                Calendar.SEPTEMBER -> "Sep"
                Calendar.OCTOBER -> "Okt"
                Calendar.NOVEMBER -> "Nov"
                Calendar.DECEMBER -> "Des"
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
     * Menentukan tipe icon cuaca berdasarkan weathercode
     * Menggunakan WeatherCodeMapper untuk mapping icon
     */
    private fun getIconTypeFromWeatherCode(weathercode: Int): WeatherIconType {
        // Mapping weathercode ke WeatherIconType kita
        return when (weathercode) {
            0 -> WeatherIconType.PAGI                    // Cerah
            1, 2 -> WeatherIconType.SIANG                // Cerah Berawan
            3 -> WeatherIconType.SORE                    // Berawan
            in 45..48 -> WeatherIconType.SORE            // Berkabut
            in 51..99 -> WeatherIconType.MALAM           // Hujan/Salju/Petir
            else -> WeatherIconType.SIANG
        }
    }
}
