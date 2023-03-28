package com.udacity.asteroidradar.respository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val asteroidDatabase: AsteroidsDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(
            asteroidDatabase.asteroidDao.getAsteroidsFromToday(
                getNextSevenDaysFormattedDates()[0]
            )
        ) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        val days = getNextSevenDaysFormattedDates()
        withContext(Dispatchers.IO) {
            val response = NasaApi.retrofitService.getAsteroids(
                startDate = days.first(),
                endDate = days.last()
            )
            if (response.isSuccessful) {
                val jsonObject = JSONObject(response.body()!!)
                val container = NetworkAsteroidContainer(parseAsteroidsJsonResult(jsonObject))
                asteroidDatabase.asteroidDao.insertAll(*container.asDatabaseModel())
            } else {
                throw Exception("API calls error")
            }
        }
    }
}