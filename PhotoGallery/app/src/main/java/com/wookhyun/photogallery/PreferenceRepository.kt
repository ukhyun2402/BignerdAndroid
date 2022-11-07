package com.wookhyun.photogallery

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import retrofit2.http.Query

class PreferenceRepository(
    private val datastore: DataStore<androidx.datastore.preferences.core.Preferences>
) {
    val storedQuery: Flow<String> = datastore.data.map {
        it[SEARCH_QUERY_KEY] ?: ""
    }.distinctUntilChanged()

    suspend fun setStoredQuery(query: String) {
        datastore.edit {
            it[SEARCH_QUERY_KEY] = query
        }
    }

    val lastResultId: Flow<String> = datastore.data.map {
        it[PREF_LAST_RESULT_ID] ?: ""
    }

    suspend fun setLastResultId(lastResultId: String) {
        datastore.edit {
            it[PREF_LAST_RESULT_ID] = lastResultId
        }
    }

    companion object {
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
        private val PREF_LAST_RESULT_ID = stringPreferencesKey("lastResultId")
    }
}