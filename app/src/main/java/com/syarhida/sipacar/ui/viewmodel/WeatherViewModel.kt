package com.syarhida.sipacar.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syarhida.sipacar.data.model.DailyWeatherCard
import com.syarhida.sipacar.data.model.HourlyWeatherItem
import com.syarhida.sipacar.data.repository.WeatherRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel untuk mengelola data cuaca
 * dan komunikasi antara UI dengan Repository
 */
class WeatherViewModel : ViewModel() {
    
    private val repository = WeatherRepository()
    
    // LiveData untuk card cuaca harian
    private val _dailyWeatherCards = MutableLiveData<List<DailyWeatherCard>>()
    val dailyWeatherCards: LiveData<List<DailyWeatherCard>> = _dailyWeatherCards
    
    // LiveData untuk cuaca saat ini (jam sekarang)
    private val _currentWeather = MutableLiveData<HourlyWeatherItem?>()
    val currentWeather: LiveData<HourlyWeatherItem?> = _currentWeather
    
    // LiveData untuk list cuaca per jam
    private val _hourlyWeatherItems = MutableLiveData<List<HourlyWeatherItem>>()
    val hourlyWeatherItems: LiveData<List<HourlyWeatherItem>> = _hourlyWeatherItems
    
    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData untuk error message
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // State untuk tanggal yang dipilih (default: hari ini)
    private var selectedDate: String? = null
    
    /**
     * Memuat data prakiraan cuaca dari API
     */
    fun loadWeatherData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // Load current weather (jam sekarang)
                val currentResult = repository.getCurrentWeather()
                if (currentResult.isSuccess) {
                    _currentWeather.value = currentResult.getOrNull()
                }
                
                // Load daily weather cards
                val dailyResult = repository.getDailyWeatherCards(selectedDate)
                if (dailyResult.isSuccess) {
                    _dailyWeatherCards.value = dailyResult.getOrNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat data. Periksa koneksi internet."
                }
                
                // Load hourly weather items untuk tanggal yang dipilih
                val hourlyResult = repository.getHourlyWeatherItems(selectedDate)
                if (hourlyResult.isSuccess) {
                    _hourlyWeatherItems.value = hourlyResult.getOrNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat data. Periksa koneksi internet."
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data. Periksa koneksi internet."
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Set tanggal yang dipilih dan reload hourly data
     */
    fun selectDate(dateString: String) {
        selectedDate = dateString
        loadWeatherData()
    }
    
    /**
     * Get current date in format YYYY-MM-DD
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
    
    /**
     * Reset error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
