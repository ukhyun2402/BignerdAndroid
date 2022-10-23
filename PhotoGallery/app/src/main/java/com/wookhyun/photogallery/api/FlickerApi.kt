package com.wookhyun.photogallery.api

import com.wookhyun.photogallery.FlickrResponse
import com.wookhyun.photogallery.PhotoResponse
import retrofit2.http.GET

const val API_KEY = "e28fce48448ac2ff85ef8f18414166f5"

interface FlickerApi {

    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
                "&api_key=$API_KEY" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    suspend fun fetchPhotos(): FlickrResponse
}