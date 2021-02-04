package com.bladerco.jmappy.network

import com.bladerco.jmappy.model.data.address.AddressResult
import com.bladerco.jmappy.model.data.places.PlacesResponse
import com.bladerco.jmappy.util.Constants.Companion.ADDRESS
import com.bladerco.jmappy.util.Constants.Companion.ADDRESS_PATH
import com.bladerco.jmappy.util.Constants.Companion.KEY
import com.bladerco.jmappy.util.Constants.Companion.LOCATION
import com.bladerco.jmappy.util.Constants.Companion.MAX_WIDTH
import com.bladerco.jmappy.util.Constants.Companion.PHOTO_PATH
import com.bladerco.jmappy.util.Constants.Companion.PHOTO_REF
import com.bladerco.jmappy.util.Constants.Companion.PLACE_PATH
import com.bladerco.jmappy.util.Constants.Companion.RADIUS
import com.bladerco.jmappy.util.Constants.Companion.TYPES
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesAPI {
    @GET(PLACE_PATH)
    fun searchNearBy(
        @Query(LOCATION) searchLocation: String,
        @Query(RADIUS) searchRadius: String,
        @Query(KEY) apiKey: String
    ): Observable<PlacesResponse>

    @GET(ADDRESS_PATH)
    fun searchAddress(
        @Query(ADDRESS) searchAddress: String,
        @Query(KEY) apiKey: String
    ): Observable<AddressResult>

    @GET(PLACE_PATH)
    fun searchTypeNearBy(
        @Query(LOCATION) searchLocation: String,
        @Query(RADIUS) searchRadius: String,
        @Query(TYPES) searchType: String,
        @Query(KEY) apiKey: String
    ): Observable<PlacesResponse>

    /*@GET(PHOTO_PATH)
    fun searchPhotoPlace(
        @Query(MAX_WIDTH) maxWidth: String,
        @Query(KEY) apiKey: String,
        @Query(PHOTO_REF) searchPhotoRef: String
    )*/
}