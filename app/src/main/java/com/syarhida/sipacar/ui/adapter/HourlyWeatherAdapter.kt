package com.syarhida.sipacar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syarhida.sipacar.data.model.HourlyWeatherItem
import com.syarhida.sipacar.databinding.ItemHourlyWeatherBinding
import com.syarhida.sipacar.util.WeatherCodeMapper

/**
 * Adapter untuk list cuaca per jam
 */
class HourlyWeatherAdapter : ListAdapter<HourlyWeatherItem, HourlyWeatherAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHourlyWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ViewHolder(
        private val binding: ItemHourlyWeatherBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: HourlyWeatherItem) {
            // Set icon cuaca berdasarkan weathercode
            val iconRes = WeatherCodeMapper.getWeatherIcon(item.weathercode)
            binding.ivWeatherIcon.setImageResource(iconRes)
            
            // Set text
            binding.tvTime.text = item.time
            binding.tvTemperature.text = item.temperature
            binding.tvWeatherDesc.text = item.weatherDesc
            binding.tvHumidity.text = item.humidity
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<HourlyWeatherItem>() {
        override fun areItemsTheSame(oldItem: HourlyWeatherItem, newItem: HourlyWeatherItem): Boolean {
            return oldItem.time == newItem.time
        }
        
        override fun areContentsTheSame(oldItem: HourlyWeatherItem, newItem: HourlyWeatherItem): Boolean {
            return oldItem == newItem
        }
    }
}

