package com.theathletic.ui.gallery.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.theathletic.R
import com.theathletic.graphic.GlideApp

class ImageGalleryAdapter(val context: Context) : RecyclerView.Adapter<ImageGalleryAdapter.SingleImageViewHolder>() {

    private var imageList = emptyList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_image_gallery, parent, false)
        return SingleImageViewHolder(view.findViewById(R.id.image))
    }

    override fun onBindViewHolder(holder: SingleImageViewHolder, position: Int) {
        GlideApp.with(context)
            .load(imageList[position])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun setImages(images: List<String>) {
        imageList = images
        notifyDataSetChanged()
    }

    data class SingleImageViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image.rootView)
}