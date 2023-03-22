package com.udacity.shoestore.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class ShoesViewModel : ViewModel() {

    private val _shoesList = MutableLiveData<MutableList<Shoe>>()
    val shoesList: LiveData<MutableList<Shoe>>
        get() = _shoesList

    lateinit var shoesName: String
    lateinit var company: String
    lateinit var shoesSize: String
    lateinit var description: String

    init {
        _shoesList.value = mutableListOf()
        resetValue()
    }

    fun addShoes() {
        _shoesList.value?.add(Shoe(shoesName, shoesSize.toDouble(), company, description))
        Timber.d("Shoes list ${_shoesList.value}")
    }

    fun resetValue() {
        shoesName = ""
        company = ""
        shoesSize = ""
        description = ""
    }

}