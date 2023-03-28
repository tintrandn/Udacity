package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.respository.AsteroidRepository
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _status = MutableLiveData<ApiStatus>()

    val status: LiveData<ApiStatus>
        get() = _status

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()

    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    val asteroids: LiveData<List<Asteroid>>
        get() = asteroidRepository.asteroids

    private val _navigateToSelectedProperty = MutableLiveData<Asteroid?>()

    val navigateToSelectedProperty: LiveData<Asteroid?>
        get() = _navigateToSelectedProperty

    fun displayPropertyDetails(asteroid: Asteroid) {
        _navigateToSelectedProperty.value = asteroid
    }

    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

    init {
        getPictureOfDay()
        getAsteroids()
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                _pictureOfDay.value = NasaApi.retrofitService.getImageOfDay()
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                _pictureOfDay.value = null
            }
        }
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                asteroidRepository.refreshAsteroids()
                _status.value = ApiStatus.DONE
            } catch (e: java.lang.Exception) {
                _status.value = ApiStatus.ERROR
            }

        }
    }

}