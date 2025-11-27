package com.syarhida.sipacar.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syarhida.sipacar.data.model.WeatherItem
import com.syarhida.sipacar.data.repository.WeatherRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola data cuaca
 * dan komunikasi antara UI dengan Repository
 */
class WeatherViewModel : ViewModel() {
    
    private val repository = WeatherRepository()
    
    // LiveData untuk daftar cuaca
    private val _weatherList = MutableLiveData<List<WeatherItem>>()
    val weatherList: LiveData<List<WeatherItem>> = _weatherList
    
    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData untuk error message
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    /**
     * Memuat data prakiraan cuaca dari API
     */
    fun loadWeatherData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = repository.getWeatherForecast()
                
                if (result.isSuccess) {
                    _weatherList.value = result.getOrNull() ?: emptyList()
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
     * Reset error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

