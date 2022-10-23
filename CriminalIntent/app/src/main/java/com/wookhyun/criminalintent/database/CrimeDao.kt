package com.wookhyun.criminalintent.database

import androidx.room.*
import com.wookhyun.criminalintent.Crime
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface CrimeDao {
    @Query("SELECT * FROM CRIME")
    fun getCrimes(): Flow<List<Crime>>

    @Query("SELECT * FROM CRIME WHERE ID=(:id)")
    suspend fun getCrime(id: UUID): Crime

    @Update
    suspend fun updateCrime(crime: Crime)

    @Insert
    suspend fun addCrime(crime: Crime)

    @Delete
    suspend fun deleteCrime(crime: Crime)
}