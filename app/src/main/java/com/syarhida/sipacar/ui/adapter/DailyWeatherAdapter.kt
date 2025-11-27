package com.syarhida.sipacar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syarhida.sipacar.R
import com.syarhida.sipacar.data.model.DailyWeatherCard
import com.syarhida.sipacar.databinding.ItemDailyWeatherBinding
import com.syarhida.sipacar.util.WeatherCodeMapper

/**
 * Adapter untuk card cuaca harian (4 hari ke depan)
 */
class DailyWeatherAdapter(
    private val onCardClick: (String) -> Unit // Callback dengan dateString
) : ListAdapter<DailyWeatherCard, DailyWeatherAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDailyWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onCardClick)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ViewHolder(
        private val binding: ItemDailyWeatherBinding,
        private val onCardClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: DailyWeatherCard) {
            // Set icon cuaca emoji berdasarkan weathercode
            val iconEmoji = WeatherCodeMapper.getWeatherIcon(item.weathercode)
            binding.tvWeatherIcon.text = iconEmoji
            
            // Set text
            binding.tvDate.text = item.date
            binding.tvTemperature.text = item.temperature
            binding.tvHumidity.text = item.humidity
            
            // Styling berdasarkan state (selected atau tidak)
            if (item.isSelected) {
                // Card yang dipilih: background biru
                binding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.primary)
                )
                binding.tvDate.setTextColor(
                    ContextCompat.getColor(binding.root.context, android.R.color.white)
                )
                binding.tvTemperature.setTextColor(
                    ContextCompat.getColor(binding.root.context, android.R.color.white)
                )
                binding.tvHumidity.setTextColor(
                    ContextCompat.getColor(binding.root.context, android.R.color.white)
                )
            } else {
                // Card tidak dipilih: background light blue
                binding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.card_background)
                )
                binding.tvDate.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_primary)
                )
                binding.tvTemperature.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_primary)
                )
                binding.tvHumidity.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                )
            }
            
            // Set click listener
            binding.root.setOnClickListener {
                onCardClick(item.dateString)
            }
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<DailyWeatherCard>() {
        override fun areItemsTheSame(oldItem: DailyWeatherCard, newItem: DailyWeatherCard): Boolean {
            return oldItem.dateString == newItem.dateString
        }
        
        override fun areContentsTheSame(oldItem: DailyWeatherCard, newItem: DailyWeatherCard): Boolean {
            return oldItem == newItem
        }
    }
}
