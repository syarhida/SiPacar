package com.syarhida.sipacar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syarhida.sipacar.R
import com.syarhida.sipacar.data.model.WeatherIconType
import com.syarhida.sipacar.data.model.WeatherItem
import com.syarhida.sipacar.databinding.ItemWeatherBinding

/**
 * Adapter untuk RecyclerView yang menampilkan list cuaca
 * Menggunakan ListAdapter dengan DiffUtil untuk performa optimal
 */
class WeatherAdapter : ListAdapter<WeatherItem, WeatherAdapter.WeatherViewHolder>(WeatherDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    /**
     * ViewHolder untuk item cuaca
     */
    class WeatherViewHolder(
        private val binding: ItemWeatherBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: WeatherItem) {
            // Set waktu
            binding.tvTime.text = item.time
            
            // Set suhu
            binding.tvTemperature.text = item.temperature
            
            // Set icon berdasarkan tipe
            val iconRes = when (item.iconType) {
                WeatherIconType.PAGI -> R.drawable.ic_weather_morning
                WeatherIconType.SIANG -> R.drawable.ic_weather_day
                WeatherIconType.SORE -> R.drawable.ic_weather_evening
                WeatherIconType.MALAM -> R.drawable.ic_weather_night
            }
            binding.ivWeatherIcon.setImageResource(iconRes)
        }
    }
    
    /**
     * DiffUtil callback untuk membandingkan item
     * Meningkatkan performa update list
     */
    private class WeatherDiffCallback : DiffUtil.ItemCallback<WeatherItem>() {
        override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
            return oldItem.time == newItem.time
        }
        
        override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
            return oldItem == newItem
        }
    }
}

