package com.syarhida.sipacar.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.syarhida.sipacar.R
import com.syarhida.sipacar.databinding.ActivityMainBinding
import com.syarhida.sipacar.ui.adapter.WeatherAdapter
import com.syarhida.sipacar.ui.viewmodel.WeatherViewModel

/**
 * Activity utama untuk menampilkan prakiraan cuaca Jakarta
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var weatherAdapter: WeatherAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Setup Toolbar
        setupToolbar()
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Setup Observers
        setupObservers()
        
        // Setup SwipeRefreshLayout
        setupSwipeRefresh()
        
        // Muat data cuaca pertama kali
        viewModel.loadWeatherData()
    }
    
    /**
     * Setup Toolbar dengan judul dan subtitle
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.toolbar_title)
            subtitle = getString(R.string.toolbar_subtitle)
        }
    }
    
    /**
     * Setup RecyclerView dengan adapter dan layout manager
     */
    private fun setupRecyclerView() {
        weatherAdapter = WeatherAdapter()
        binding.recyclerView.apply {
            adapter = weatherAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }
    
    /**
     * Setup SwipeRefreshLayout untuk pull-to-refresh
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadWeatherData()
        }
        
        // Set warna indicator sesuai tema
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.primary)
    }
    
    /**
     * Setup observers untuk LiveData dari ViewModel
     */
    private fun setupObservers() {
        // Observer untuk daftar cuaca
        viewModel.weatherList.observe(this) { weatherList ->
            weatherAdapter.submitList(weatherList)
            
            // Tampilkan atau sembunyikan empty state
            if (weatherList.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
        
        // Observer untuk status loading
        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
            
            // Tampilkan atau sembunyikan progress bar
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvLoading.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.tvLoading.visibility = View.GONE
            }
        }
        
        // Observer untuk error message
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                binding.tvEmptyState.text = it
                binding.tvEmptyState.visibility = View.VISIBLE
                viewModel.clearError()
            }
        }
    }
}

