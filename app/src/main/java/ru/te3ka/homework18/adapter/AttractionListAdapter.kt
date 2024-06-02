package ru.te3ka.homework18.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.te3ka.homework18.R
import ru.te3ka.homework18.model.Attraction
import com.bumptech.glide.Glide
import ru.te3ka.homework18.databinding.LayoutItemAttractionBinding

class AttractionListAdapter : ListAdapter<Attraction, AttractionListAdapter.AttractionViewHolder>(AttractionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val binding = LayoutItemAttractionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttractionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = getItem(position)
        holder.bind(attraction)
    }

    class AttractionViewHolder(private val binding: LayoutItemAttractionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(attraction: Attraction) {
            binding.attraction = attraction
            binding.executePendingBindings()
            binding.textDatePhoto.text = attraction.dateTaken

            Glide.with(binding.root)
                .load(attraction.photoPath)
                .into(binding.imageAttraction)
        }
    }
}

class AttractionDiffCallback : DiffUtil.ItemCallback<Attraction>() {

    override fun areItemsTheSame(oldItem: Attraction, newItem: Attraction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Attraction, newItem: Attraction): Boolean {
        return oldItem == newItem
    }
}