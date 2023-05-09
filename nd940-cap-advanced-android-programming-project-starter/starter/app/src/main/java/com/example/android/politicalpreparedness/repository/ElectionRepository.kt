package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election

class ElectionRepository(private val electionDatabase: ElectionDatabase) {

    fun getAllElections(): LiveData<List<Election>> {
        return electionDatabase.electionDao.getAllElections()
    }

    suspend fun getElectionById(electionId: Int): Election? {
        return electionDatabase.electionDao.getElectionById(electionId)
    }

    suspend fun insertElection(selectedElection: Election) {
        return electionDatabase.electionDao.insertElection(selectedElection)
    }

    suspend fun deleteElection(selectedElection: Election) {
        return electionDatabase.electionDao.deleteElection(selectedElection)
    }

}