package com.bladerco.jmappy.network

import com.bladerco.jmappy.model.data.address.AddressResult
import com.bladerco.jmappy.model.data.places.PlacesResponse
import com.bladerco.jmappy.util.Constants.Companion.BASE_URL
import com.bladerco.jmappy.util.Constants.Companion.MY_API_KEY
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MapsRetrofit {

    private var mapApi: PlacesAPI

    init {
        mapApi = createMapsApi(createRetrofit())
    }

    private fun createMapsApi(retrofit: Retrofit): PlacesAPI {
        return retrofit.create(PlacesAPI::class.java)
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun getNearBy(location: String, radius: String): Observable<PlacesResponse> =
        mapApi.searchNearBy(location, radius, MY_API_KEY)

    fun getAddress(address: String): Observable<AddressResult> =
        mapApi.searchAddress(address, MY_API_KEY)

    fun getTypeNearBy(location: String, radius: String, type: String): Observable<PlacesResponse> =
        mapApi.searchTypeNearBy(location, radius, type, MY_API_KEY)

}