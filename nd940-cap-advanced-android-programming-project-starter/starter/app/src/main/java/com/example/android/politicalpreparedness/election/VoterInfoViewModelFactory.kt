package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.network.models.Election

class VoterInfoViewModelFactory(
    private val application: Application,
    private val selectedElection: Election
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) return VoterInfoViewModel(
            application,
            selectedElection
        ) as T
        throw IllegalArgumentException("Unknown viewModel class")
    }
}