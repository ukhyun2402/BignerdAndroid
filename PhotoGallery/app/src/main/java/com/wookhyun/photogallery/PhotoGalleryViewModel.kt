package com.wookhyun.photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.*
import java.util.logging.Handler
import javax.inject.Inject

private const val TAG = "PhotoGalleryViewModel"

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<PhotoGalleryUiState> =
        MutableStateFlow(PhotoGalleryUiState())
    val uiState: StateFlow<PhotoGalleryUiState>
        get() = _uiState.asStateFlow()

    var currentPage = 1
    var loadingState: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        viewModelScope.launch {
            preferenceRepository.storedQuery.collectLatest { storedQuery ->
                try {
                    loadingState.value = true
                    val items = fetchGalleryItems(storedQuery).toMutableList()
                    _uiState.update { oldState ->
                        oldState.copy(
                            image = items,
                            query = storedQuery
                        )
                    }
                    delay(2000)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to fetch Data: $e")
                } finally {
                    loadingState.value = false
                }
            }
        }
    }

    fun fetchNextPhotos() {
        viewModelScope.launch {
            if (currentPage < 5) {
                _uiState.value.image += photoRepository.fetchPhotos(++currentPage)
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

data class PhotoGalleryUiState(
    val image: MutableList<GalleryItem> = mutableListOf<GalleryItem>(), val query: String = ""
)