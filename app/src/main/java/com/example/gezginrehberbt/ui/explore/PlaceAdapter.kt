package com.example.gezginrehberbt.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.gezginrehberbt.databinding.ItemPlaceBinding
import com.example.gezginrehberbt.model.Place

class PlaceAdapter(
    private val items: List<Place>,
    private val onItemClick: (Place) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(val binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = items[position]
        
        // Thumbnail için optimize edilmiş görüntüleme
        // Merkezi kırpma ve köşeleri yuvarlatma eklendi
        val requestOptions = RequestOptions()
            .transform(CenterCrop(), RoundedCorners(16)) // 16dp yuvarlak köşeler
            .override(300, 200) // Thumbnail boyutu sabitlendi
        
        Glide.with(holder.binding.imageView)
            .load(place.imageResId)
            .apply(requestOptions)
            .into(holder.binding.imageView)
            
        holder.binding.tvPlaceName.text = place.name
        holder.binding.tvDistance.text = place.distance
        holder.binding.ratingBarSmall.rating = place.rating.toFloat()
        holder.binding.tvCategory.text = place.category
        holder.itemView.setOnClickListener { onItemClick(place) }
    }

    override fun getItemCount() = items.size
}
