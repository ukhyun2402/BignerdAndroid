package com.wookhyun.photogallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PhotoGalleryViewModel"

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _galleryItems: MutableStateFlow<List<GalleryItem>> = MutableStateFlow(emptyList())
    val galleryItems: StateFlow<List<GalleryItem>>
        get() = _galleryItems.asStateFlow()
    var currentPage = 1

    init {
        viewModelScope.launch {
            preferenceRepository.storedQuery.collectLatest { storedQuery ->
                try {
                    val items = fetchGalleryItems(storedQuery)
                    _galleryItems.value = items
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to fetch Data: $e")
                }
            }
        }
    }

    fun fetchNextPhotos() {
        viewModelScope.launch {
            if (currentPage < 5) {
                _galleryItems.value += photoRepository.fetchPhotos(++currentPage)
                Log.d(TAG, "fetchNextPhotos: current galliery size = ${galleryItems.value.size}")
            }
        }
    }

    fun setQuery(query: String) {
        viewModelScope.launch {
            preferenceRepository.setStoredQuery(query)
        }
    }

    suspend fun fetchGalleryItems(query: String): List<GalleryItem> {
        return if (query.isNotEmpty()) {
            photoRepository.searchPhotos(query)
        } else {
            photoRepository.fetchPhotos(currentPage)
        }
    }
}