package com.wookhyun.photogallery.module

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.wookhyun.photogallery.PhotoRepository
import com.wookhyun.photogallery.PreferenceRepository
import com.wookhyun.photogallery.api.PhotoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()

        val retrofit = Retrofit.Builder().baseUrl("https://api.flickr.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
        return PhotoRepository(retrofit.create())
    }

    @Singleton
    @Provides
    fun provideDatastore(@ApplicationContext appContext: Context): PreferenceRepository {
        val instance = PreferenceDataStoreFactory.create {
            appContext.preferencesDataStoreFile("settings")
        }
        return PreferenceRepository(instance)
    }
}