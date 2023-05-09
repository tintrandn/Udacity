package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val savedStateAddressObserver = Observer<Address> {
        address.value = it
        fetchRepresentatives()
    }

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    val address = MutableLiveData(Address("", "", "", "", ""))

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    init {
        savedStateHandle.getLiveData<Address>("address").observeForever(savedStateAddressObserver)
    }

    fun setCurrentAddress(currentAddress: Address) {
        address.value = currentAddress
    }

    fun fetchRepresentatives() {
        viewModelScope.launch {
            try {
                if (address.value != null) {
                    val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address.value!!.toFormattedString())
                    _representatives.value = offices.flatMap { office ->
                        office.getRepresentatives(officials)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load representatives ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        savedStateHandle.getLiveData<Address>("address").removeObserver(savedStateAddressObserver)
    }
}
