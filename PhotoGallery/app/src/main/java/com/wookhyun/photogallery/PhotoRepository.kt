package com.wookhyun.photogallery

import com.wookhyun.photogallery.api.FlickerApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    val flickerApi: FlickerApi
) {
    suspend fun fetchPhotos() = flickerApi.fetchPhotos().photos.galleryItems
}