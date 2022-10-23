package com.wookhyun.photogallery

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoResponse(
    val page: Int,
    val pages: Int,
    @Json(name = "perpage") val perPage: Int,
    val total: Int,
    @Json(name = "photo") val galleryItems: List<GalleryItem>
)
