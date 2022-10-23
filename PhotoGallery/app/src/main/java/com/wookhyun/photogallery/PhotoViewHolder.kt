package com.wookhyun.photogallery

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wookhyun.photogallery.databinding.ListItemGalleryBinding

class PhotoViewHolder(
    private val binding: ListItemGalleryBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(galleryItem: GalleryItem) {
        binding.itemImageView.load(galleryItem.url){
            placeholder(R.drawable.bill_up_close)
        }
    }
}