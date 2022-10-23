package com.wookhyun.photogallery.module

import com.wookhyun.photogallery.PhotoRepository
import com.wookhyun.photogallery.api.FlickerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object MainModule {

    @Singleton
    @Provides
    fun provideRepository(): PhotoRepository {
        val retrofit = Retrofit.Builder().baseUrl("https://api.flickr.com/")
            .addConverterFactory(MoshiConverterFactory.create()).build()
        return PhotoRepository(retrofit.create())
    }


}