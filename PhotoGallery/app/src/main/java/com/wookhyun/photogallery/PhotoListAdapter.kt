package com.wookhyun.photogallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wookhyun.photogallery.databinding.ListItemGalleryBinding

private const val TAG = "PhotoListAdapter"
class PhotoListAdapter(
    private val photoGalleryViewModel: PhotoGalleryViewModel
) : RecyclerView.Adapter<PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflater, parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = photoGalleryViewModel.uiState.value.image[position]
        holder.bind(item)

        if(position == photoGalleryViewModel.uiState.value.image.size - 1) {
            photoGalleryViewModel.fetchNextPhotos()
        }
    }

    override fun getItemCount() = photoGalleryViewModel.uiState.value.image.size
}