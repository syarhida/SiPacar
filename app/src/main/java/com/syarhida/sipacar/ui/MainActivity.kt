package com.syarhida.sipacar.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.syarhida.sipacar.R
import com.syarhida.sipacar.databinding.ActivityMainBinding
import com.syarhida.sipacar.ui.adapter.DailyWeatherAdapter
import com.syarhida.sipacar.ui.adapter.HourlyWeatherAdapter
import com.syarhida.sipacar.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity utama untuk menampilkan prakiraan cuaca Jakarta
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var dailyAdapter: DailyWeatherAdapter
    private lateinit var hourlyAdapter: HourlyWeatherAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Setup RecyclerViews
        setupRecyclerViews()
        
        // Setup Observers
        setupObservers()
        
        // Setup SwipeRefreshLayout
        setupSwipeRefresh()
        
        // Update waktu saat ini
        updateCurrentTime()
        
        // Muat data cuaca pertama kali
        viewModel.loadWeatherData()
    }
    
    /**
     * Setup RecyclerViews untuk daily dan hourly forecast
     */
    private fun setupRecyclerViews() {
        // Daily forecast (horizontal)
        dailyAdapter = DailyWeatherAdapter()
        binding.rvDailyForecast.apply {
            adapter = dailyAdapter
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
        
        // Hourly forecast (vertical)
        hourlyAdapter = HourlyWeatherAdapter()
        binding.rvHourlyForecast.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }
    
    /**
     * Setup SwipeRefreshLayout untuk pull-to-refresh
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            updateCurrentTime()
            viewModel.loadWeatherData()
        }
        
        // Set warna indicator sesuai tema
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.primary)
    }
    
    /**
     * Update waktu saat ini
     */
    private fun updateCurrentTime() {
        val currentTime = SimpleDateFormat("HH.mm.ss", Locale.getDefault()).format(Date())
        binding.tvCurrentTime.text = "Jam saat ini: $currentTime WIB"
    }
    
    /**
     * Setup observers untuk LiveData dari ViewModel
     */
    private fun setupObservers() {
        // Observer untuk daily weather cards
        viewModel.dailyWeatherCards.observe(this) { cards ->
            dailyAdapter.submitList(cards)
            
            // Update current weather card dengan data hari ini (card pertama)
            if (cards.isNotEmpty()) {
                val today = cards[0]
                binding.tvCurrentTemp.text = today.temperature.replace("°", "°C")
                binding.tvCurrentDesc.text = "Berawan"
                binding.tvCurrentHumidity.text = today.humidity
                
                // Set icon
                val iconRes = when (today.iconType) {
                    com.syarhida.sipacar.data.model.WeatherIconType.PAGI -> R.drawable.ic_weather_morning
                    com.syarhida.sipacar.data.model.WeatherIconType.SIANG -> R.drawable.ic_weather_day
                    com.syarhida.sipacar.data.model.WeatherIconType.SORE -> R.drawable.ic_weather_evening
                    com.syarhida.sipacar.data.model.WeatherIconType.MALAM -> R.drawable.ic_weather_night
                }
                binding.ivCurrentWeatherIcon.setImageResource(iconRes)
            }
        }
        
        // Observer untuk hourly weather items
        viewModel.hourlyWeatherItems.observe(this) { items ->
            hourlyAdapter.submitList(items)
        }
        
        // Observer untuk status loading
        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
            binding.loadingLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observer untuk error message
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
}
