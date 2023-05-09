package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch
import java.lang.Exception

enum class ApiStatus { LOADING, ERROR, DONE }

class ElectionsViewModel(application: Application) : ViewModel() {

    private val database = getInstance(application)
    private val electionRepository = ElectionRepository(database)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private lateinit var _savedElections: LiveData<List<Election>>
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    fun displayVoterInfo(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun displayVoterInfoCompleted() {
        _navigateToVoterInfo.value = null
    }

    init {
        fetchUpcomingElections()
        fetchSavedElections()
    }

    private fun fetchUpcomingElections() {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                val electionResponse = CivicsApi.retrofitService.getElections()
                if (electionResponse.elections.isNotEmpty())
                    _upcomingElections.value = electionResponse.elections
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
            }
        }
    }

    fun fetchSavedElections() {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                _savedElections = electionRepository.getAllElections()
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
            }
        }
    }
}