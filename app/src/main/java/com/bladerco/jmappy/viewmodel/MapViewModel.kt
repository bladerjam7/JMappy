package com.bladerco.jmappy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bladerco.jmappy.model.MapRepository
import com.bladerco.jmappy.model.data.address.Location
import com.bladerco.jmappy.model.data.places.PlacesResult

class MapViewModel(application: Application): AndroidViewModel(application) {

    private val repository = MapRepository()

    val placesLiveData: MutableLiveData<List<PlacesResult>> = repository.placesLiveData
    val addressLiveData: MutableLiveData<Location> = repository.addressLiveData
    val placeTypeLiveData: MutableLiveData<List<PlacesResult>> = repository.placesTypeLiveData

    fun getPlaces(location: String, radius: String){
        repository.getPlacesResults(location, radius)
    }

    fun getAddress(address: String){
        repository.getAddressResults(address)
    }

    fun getPlacesType(location: String, radius: String, type: String){
        repository.getPlacesTypeResults(location, radius, type)
    }



}