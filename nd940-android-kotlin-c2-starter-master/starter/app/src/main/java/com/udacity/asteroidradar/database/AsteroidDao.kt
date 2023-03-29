package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroid_list WHERE date(closeApproachDate) >= date(:today) ORDER BY date(closeApproachDate)")
    fun getAsteroidsForAWeek(today: String): LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroid_list WHERE date(closeApproachDate) == date(:today) LIMIT 5")
    fun getAsteroidsForToday(today: String): LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroid_list ORDER BY date(closeApproachDate)")
    fun getAllAsteroids(): LiveData<List<AsteroidEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: AsteroidEntity)
}