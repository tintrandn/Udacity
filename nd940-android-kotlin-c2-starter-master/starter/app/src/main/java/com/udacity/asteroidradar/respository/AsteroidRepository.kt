package com.udacity.asteroidradar.respository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject


enum class Filter { WEEK, TODAY, ALL }

class AsteroidRepository(private val asteroidDatabase: AsteroidsDatabase) {

    val asteroids = MediatorLiveData<List<Asteroid>>()

    private var currentFilter = Filter.WEEK

    private val weekAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(
            asteroidDatabase.asteroidDao.getAsteroidsForAWeek(
                getNextSevenDaysFormattedDates()[0]
            )
        ) {
            it.asDomainModel()
        }

    private val todayAsteroid: LiveData<List<Asteroid>> =
        Transformations.map(
            asteroidDatabase.asteroidDao.getAsteroidsForToday(
                getNextSevenDaysFormattedDates()[0]
            )
        ) {
            it.asDomainModel()
        }

    private val allAsteroid: LiveData<List<Asteroid>> =
        Transformations.map(
            asteroidDatabase.asteroidDao.getAllAsteroids()
        ) {
            it.asDomainModel()
        }

    init {
        asteroids.addSource(weekAsteroids) { result ->
            if (currentFilter == Filter.WEEK) {
                result?.let { asteroids.value = it }
            }
        }
        asteroids.addSource(todayAsteroid) { result ->
            if (currentFilter == Filter.TODAY) {
                result?.let { asteroids.value = it }
            }
        }
        asteroids.addSource(allAsteroid) { result ->
            if (currentFilter == Filter.ALL) {
                result?.let { asteroids.value = it }
            }
        }
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

    fun updateAsteroids(filter: Filter) = when (filter) {
        Filter.WEEK -> weekAsteroids.value?.let { asteroids.value = it }
        Filter.TODAY -> todayAsteroid.value?.let { asteroids.value = it }
        Filter.ALL -> allAsteroid.value?.let { asteroids.value = it }
    }.also { currentFilter = filter }
}