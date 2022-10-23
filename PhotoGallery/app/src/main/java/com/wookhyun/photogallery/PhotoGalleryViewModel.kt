package com.wookhyun.photogallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PhotoGalleryViewModel"

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
): ViewModel() {

    private val _galleryItems: MutableStateFlow<List<GalleryItem>> = MutableStateFlow(emptyList())
    val galleryItems: StateFlow<List<GalleryItem>>
        get() = _galleryItems.asStateFlow()
    
    init {
        viewModelScope.launch { 
            try{
                val items = photoRepository.fetchPhotos()
                _galleryItems.value = items
            }
            catch (e: Exception){
                Log.e(TAG, "Failed to fetch Data: $e", )
            }
        }
    }

}