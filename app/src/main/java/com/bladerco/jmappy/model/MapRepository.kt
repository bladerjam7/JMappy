package com.bladerco.jmappy.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bladerco.jmappy.model.data.address.Location
import com.bladerco.jmappy.model.data.places.PlacesResult
import com.bladerco.jmappy.network.MapsRetrofit
import com.bladerco.jmappy.util.Constants.Companion.TAG
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MapRepository {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val mapsRetrofit: MapsRetrofit = MapsRetrofit()

    val placesLiveData: MutableLiveData<List<PlacesResult>> = MutableLiveData()
    val addressLiveData: MutableLiveData<Location> = MutableLiveData()
    val placesTypeLiveData: MutableLiveData<List<PlacesResult>> = MutableLiveData()


    fun getPlacesResults(location: String, radius: String) {
        compositeDisposable.add(
            mapsRetrofit.getNearBy(location, radius)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map { it.results }
                .subscribe({
                    placesLiveData.postValue(it)
                    Log.d(TAG, "getPlacesResults: Getting Results")

                    compositeDisposable.clear()
                }, {
                    Log.d(TAG, "getPlacesResults: Failed Places ----------> $it")
                })
        )
    }

    fun getAddressResults(address: String) {
        compositeDisposable.add(
            mapsRetrofit.getAddress(address)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map { it.results }
                .subscribe({
                    Log.d(TAG, "getAddressResults: Address worked")
                    it.forEach { addressLiveData.postValue(it.geometry.location)  }
                    compositeDisposable.clear()
                }, {
                    Log.d(TAG, "getAddressResults: Failed Address: ----------> $it")
                    Log.d(TAG, "getAddressResults: $address")
                })
        )
    }

    fun getPlacesTypeResults(location: String, radius: String, type: String) {
        compositeDisposable.add(
            mapsRetrofit.getTypeNearBy(location, radius, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map { it.results }
                .subscribe({
                    placesTypeLiveData.postValue(it)
                    Log.d(TAG, "getPlacesResults: Getting Results")

                    compositeDisposable.clear()
                }, {
                    Log.d(TAG, "getPlacesResults: Failed Places ----------> $it")
                })
        )
    }

}