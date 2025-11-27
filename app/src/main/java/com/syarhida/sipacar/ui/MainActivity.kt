package com.syarhida.sipacar.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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
    
    // Handler untuk update jam setiap detik
    private val timeUpdateHandler = Handler(Looper.getMainLooper())
    private val timeUpdateRunnable = object : Runnable {
        override fun run() {
            updateCurrentTime()
            timeUpdateHandler.postDelayed(this, 1000) // Update setiap 1 detik
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Setup Navigation Drawer
        setupNavigationDrawer()
        
        // Setup RecyclerViews
        setupRecyclerViews()
        
        // Setup Observers
        setupObservers()
        
        // Setup SwipeRefreshLayout
        setupSwipeRefresh()
        
        // Update waktu saat ini
        updateCurrentTime()
        
        // Start auto-update waktu setiap detik
        startTimeUpdates()
        
        // Muat data cuaca pertama kali
        viewModel.loadWeatherData()
    }
    
    override fun onResume() {
        super.onResume()
        // Resume update waktu saat app aktif lagi
        stopTimeUpdates() // Stop dulu untuk menghindari dobel
        startTimeUpdates()
    }
    
    override fun onPause() {
        super.onPause()
        // Stop update waktu saat app di-pause untuk hemat resource
        stopTimeUpdates()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Stop update waktu saat activity destroy
        stopTimeUpdates()
    }
    
    /**
     * Start auto-update waktu setiap detik
     */
    private fun startTimeUpdates() {
        timeUpdateHandler.removeCallbacks(timeUpdateRunnable) // Pastikan tidak ada callback lama
        timeUpdateHandler.post(timeUpdateRunnable)
    }
    
    /**
     * Stop auto-update waktu
     */
    private fun stopTimeUpdates() {
        timeUpdateHandler.removeCallbacks(timeUpdateRunnable)
    }
    
    /**
     * Setup Navigation Drawer
     */
    private fun setupNavigationDrawer() {
        // Pastikan drawer tertutup saat pertama kali
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        
        // Menu icon click listener
        binding.ivMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        
        // GitHub link click listener
        val navDrawer = findViewById<View>(R.id.navDrawer)
        val layoutGithub = navDrawer.findViewById<View>(R.id.layoutGithub)
        layoutGithub?.setOnClickListener {
            openGithubRepository()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
    
    /**
     * Open GitHub repository in browser
     */
    private fun openGithubRepository() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/syarhida/SiPacar"))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak dapat membuka browser", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Handle back press - close drawer if open
     */
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    
    /**
     * Setup RecyclerViews untuk daily dan hourly forecast
     */
    private fun setupRecyclerViews() {
        // Daily forecast (horizontal) dengan click listener
        dailyAdapter = DailyWeatherAdapter { dateString ->
            // Ketika card diklik, update data hourly untuk tanggal tersebut
            viewModel.selectDate(dateString)
        }
        
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
        // Observer untuk current weather (jam sekarang)
        viewModel.currentWeather.observe(this) { currentWeather ->
            currentWeather?.let { weather ->
                binding.tvCurrentTemp.text = weather.temperature
                binding.tvCurrentDesc.text = weather.weatherDesc
                binding.tvCurrentHumidity.text = weather.humidity
                
                // Set icon emoji berdasarkan weathercode
                val iconEmoji = com.syarhida.sipacar.util.WeatherCodeMapper.getWeatherIcon(weather.weathercode)
                binding.tvCurrentWeatherIcon.text = iconEmoji
            }
        }
        
        // Observer untuk daily weather cards
        viewModel.dailyWeatherCards.observe(this) { cards ->
            dailyAdapter.submitList(cards)
            
            // Update label tanggal yang sedang ditampilkan
            val selectedCard = cards.find { it.isSelected } ?: cards.firstOrNull()
            selectedCard?.let { card ->
                updateSelectedDateLabel(card)
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
    
    /**
     * Update label tanggal yang sedang ditampilkan
     */
    private fun updateSelectedDateLabel(card: com.syarhida.sipacar.data.model.DailyWeatherCard) {
        if (card.isToday) {
            // Hari ini: tampilkan dari jam sekarang
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            binding.tvSelectedDate.text = "Prakiraan Cuaca Hari Ini"
            binding.tvDateInfo.text = "Menampilkan prakiraan dari jam $currentTime sampai 23:00"
        } else {
            // Hari lain: tampilkan semua jam
            binding.tvSelectedDate.text = "Prakiraan Cuaca ${card.date}"
            binding.tvDateInfo.text = "Menampilkan prakiraan dari jam 00:00 sampai 23:00"
        }
    }
}
