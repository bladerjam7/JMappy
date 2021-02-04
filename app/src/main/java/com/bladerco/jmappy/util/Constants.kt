package com.bladerco.jmappy.util

class Constants{

    // https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&key=AIzaSyDh8h-16Q-uDkppVJsfPFjqS-U2OxSyfi8&photo_reference=ATtYBwJnVu_ObYPRi74zvUSPs-3YgmyUru1uhYfrPYHKr0LTnObVstfJyoOncrJvTnJDq9EYhDllfMVnsEIHR3_9DJEriqo_F2_nFEusbaRIqiDOgzkMeQu-wZNAyriMMt8p100xg3kLhgzpk544ur9hAcwdUs7-xE4UMtj8NVlKEDb0mLSH

    companion object{
        const val IMAGE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&key=YOUR KEY HERE&photoreference="

        const val BASE_URL = "https://maps.googleapis.com/"

        // PATHS
        const val PLACE_PATH= "/maps/api/place/nearbysearch/json"
        const val ADDRESS_PATH = "/maps/api/geocode/json"
        const val PHOTO_PATH = "/maps/api/place/photo?maxwidth=400&key=YOUR KEY HERE"

        // QUERY HEADERS
        const val LOCATION= "location"
        const val RADIUS= "radius"
        const val ADDRESS = "address"
        const val KEY = "key"
        const val TYPES = "types"
        const val MAX_WIDTH = "maxwidth"
        const val PHOTO_REF = "photo_reference"

        // API KEY
        const val MY_API_KEY = "YOUR KEY HERE"

        // Request codes
        const val LOCATION_REQUEST_CODE = 70

        const val TAG = "TAG_X"

    }
}