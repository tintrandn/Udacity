package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

class VoterInfoViewModel(application: Application, val selectedElection: Election) :
    ViewModel() {

    private val database = ElectionDatabase.getInstance(application)
    private val electionRepository = ElectionRepository(database)

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()

    val correspondenceAddress: LiveData<String?> = Transformations.map(_voterInfo) {
        return@map _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
    }

    val votingLocationUrl: LiveData<String?> = Transformations.map(_voterInfo) {
        return@map _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
    }

    val ballotInfoUrl: LiveData<String?> = Transformations.map(_voterInfo) {
        return@map _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
    }

    private val _isFollowedButton = MutableLiveData<Boolean>(false)
    val isFollowedButton: LiveData<Boolean>
        get() = _isFollowedButton

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    init {
        getVoterInfo()
        checkSavedElection(selectedElection)
    }

    fun onFollowButtonClicked() {
        _isFollowedButton.value = if (_isFollowedButton.value == true) {
            unFollowCurrentElection()
            false
        } else {
            followCurrentElection()
            true
        }
    }

    private fun getVoterInfo() = viewModelScope.launch {
        try {
            var address = selectedElection.division.country
            if (selectedElection.division.state.isNotBlank()) {
                address += "/${selectedElection.division.state}"
            }

            val voterInfoResponse =
                CivicsApi.retrofitService.getVoterInfo(address, selectedElection.id.toLong())
            _voterInfo.value = voterInfoResponse

        } catch (e: Exception) {
            _error.value = "Can not get the election information.\n Please try another one!"
        }
    }

    private fun checkSavedElection(selectedElection: Election) = viewModelScope.launch {
        val election = electionRepository.getElectionById(selectedElection.id)
        _isFollowedButton.value = election != null
    }

    private fun followCurrentElection() = viewModelScope.launch {
        electionRepository.insertElection(selectedElection)
    }

    private fun unFollowCurrentElection() = viewModelScope.launch {
        electionRepository.deleteElection(selectedElection)
    }

}