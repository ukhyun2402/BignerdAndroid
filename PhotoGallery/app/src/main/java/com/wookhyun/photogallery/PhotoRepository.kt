package com.wookhyun.photogallery

import FlickrAPI
import android.util.Log
import javax.inject.Inject

private const val TAG = "PhotoRepository"

class PhotoRepository  constructor(
    val flickrAPI: FlickrAPI,
) {

    suspend fun fetchPhotos(page: Int = 1): List<GalleryItem> {
        val response = flickrAPI.fetchPhotos(page)
        return response.photos.galleryItems
    }

    suspend fun searchPhotos(query: String): List<GalleryItem> {
        val response =  flickrAPI.searchPhotos(query).photos.galleryItems
        Log.d(TAG, "searchPhotos: $response")
        return response
    }

}