package com.bladerco.jmappy.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.bladerco.jmappy.R
import com.bladerco.jmappy.model.data.places.PlacesResult
import com.bladerco.jmappy.util.Constants.Companion.LOCATION_REQUEST_CODE
import com.bladerco.jmappy.util.Constants.Companion.TAG
import com.bladerco.jmappy.view.adapter.PlacesAdapter
import com.bladerco.jmappy.viewmodel.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    NavigationView.OnNavigationItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: EncryptedSharedPreferences
    private val mapViewModel: MapViewModel by viewModels()
    private lateinit var locationManager: LocationManager
    private val placesAdapter: PlacesAdapter = PlacesAdapter(listOf())

    private var currentLat = 0.0
    private var currentLong = 0.0
    private var radius = 1000
    private var lastSelected = ""

    private lateinit var etSearchBar: EditText
    private lateinit var btnCurrentLocation: CardView
    private lateinit var btnSearchArea: Button
    private lateinit var menuView: CardView
    private lateinit var navDrawerLayout: DrawerLayout
    private lateinit var navDrawerView: NavigationView
    private lateinit var seekBarRadius: SeekBar
    private lateinit var seekBarTextView: TextView
    private lateinit var btnRadius: CardView
    private lateinit var seekBarMenu: ConstraintLayout
    private lateinit var rvPlaces: RecyclerView

    private lateinit var slide_in_bottom: Animation
    private lateinit var slide_out_bottom: Animation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        etSearchBar = findViewById(R.id.et_search_bar)
        btnCurrentLocation = findViewById(R.id.btn_reset_current)
        btnSearchArea = findViewById(R.id.btn_search_area)
        menuView = findViewById(R.id.btn_setting_menu)
        navDrawerLayout = findViewById(R.id.main_nav_drawer_layout)
        navDrawerView = findViewById(R.id.main_nav_drawer)
        seekBarRadius = findViewById(R.id.sb_radius)
        seekBarTextView = findViewById(R.id.tv_radius_number)
        seekBarMenu = findViewById(R.id.radius_menu)
        btnRadius = findViewById(R.id.btn_radius)
        rvPlaces = findViewById(R.id.rv_places)

        rvPlaces.adapter = placesAdapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rvPlaces)


        slide_in_bottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        slide_out_bottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom)

        seekBarRadius.setOnSeekBarChangeListener(this)

        navDrawerView.setNavigationItemSelectedListener(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        clickListerners()

        menuView.setOnClickListener {
            if (navDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                navDrawerLayout.closeDrawer(GravityCompat.START)
            } else {
                navDrawerLayout.openDrawer(GravityCompat.START)
            }
        }

        mapViewModel.placesLiveData.observe(this, Observer {
            Log.d(TAG, "placesLiveData size: ${it.size} ")
            it.forEach {
                if (it.rating != null && it.rating != 0.0) {
                }

            }
        })


    }

    private fun clickListerners() {
        btnCurrentLocation.setOnClickListener {
            moveCameraToLocation(currentLat, currentLong)
        }

        btnSearchArea.setOnClickListener {
            if (isNetworkConnected()) {
                getPlacesFromCamera()
            }
        }

        btnRadius.setOnClickListener {
            if (seekBarMenu.visibility == View.GONE) {
                seekBarMenu.visibility = View.VISIBLE
                seekBarMenu.startAnimation(slide_in_bottom)
            } else {
                seekBarMenu.startAnimation(slide_out_bottom)
                seekBarMenu.visibility = View.GONE

            }
        }

        etSearchBar.setOnKeyListener(View.OnKeyListener { view, i, keyEvent ->
            if(i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN){
                val address = etSearchBar.text.toString().trim().replace(" ", "+")
                mapViewModel.getAddress(address)
                Log.d(TAG, "clickListerners: Enter")

                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(etSearchBar.getWindowToken(), 0)

                mapViewModel.addressLiveData.observe(this, Observer {
                    Log.d(TAG, "clickListerners: ${it.lng}, ${it.lng}")
                    moveCameraToLocation(it.lat, it.lng)
                })
                return@OnKeyListener true
            }
            false
        })
    }

    private fun getPlacesFromCamera() {
        val cameraLat = mMap.cameraPosition.target.latitude
        val cameraLong = mMap.cameraPosition.target.longitude


        mapViewModel.getPlaces("$cameraLat, $cameraLong", "$radius")
    }

    override fun onStart() {
        super.onStart()
        requestLocationPermission()
        getCurrentLocation()
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnCameraMoveListener {
            if (rvPlaces.visibility == View.VISIBLE) {
                rvPlaces.startAnimation(slide_out_bottom)
                rvPlaces.visibility = View.GONE
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        getCurrentLocation()
        drawCurrentCircle()
    }


    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        val task: Task<Location> = fusedLocationProviderClient.lastLocation


        task.addOnSuccessListener {
            if (it != null) {
                currentLat = it.latitude
                currentLong = it.longitude

                createSharePreference()

                sharedPreferences
                    .edit()
                    .putString("lat", "$currentLat")
                    .putString("long", "$currentLong")
                    .apply()

                moveCameraToLocation(currentLat, currentLong)
                drawCurrentCircle()


            }
        }
    }

    private fun moveCameraToLocation(lat: Double, long: Double) {
        if (this::mMap.isInitialized) {
            val latlong = LatLng(lat, long)

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15f))
        }

    }

    private fun drawCurrentCircle() {
        if (this::mMap.isInitialized) {
            Log.d(TAG, "drawCurrentCircle: $currentLat, $currentLong")
            mMap.addCircle(
                CircleOptions()
                    .center(LatLng(currentLat, currentLong))
                    .radius(40.0)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.BLUE)

            )
        } else {
            Log.d(TAG, "drawCurrentCircle: mMap Not Initialized")
        }

    }

    private fun drawRadiusCircle(lat: Double, long: Double) {
        if (this::mMap.isInitialized) {
            mMap.addCircle(
                CircleOptions()
                    .center(LatLng(lat, long))
                    .radius(radius.toDouble())
                    .strokeColor(Color.parseColor("#66F57C00"))
                    .fillColor(Color.parseColor("#4D52C8FF"))
            )
        } else {
            Log.d(TAG, "drawRadiusCircle: mMap Not Initialized")
        }

    }

    @Suppress("DEPRECATION")
    private fun createSharePreference() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        sharedPreferences = EncryptedSharedPreferences.create(
            "current_location",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }


    /*********  Network Check   ***************/

    @Suppress("DEPRECATION")
    private fun isNetworkConnected(): Boolean {
        val cm: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo?.let {
            it.isConnected
        } ?: false
    }


    /*********  Permissions Check   ***************/

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (LOCATION_REQUEST_CODE == requestCode) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, " -----> Location Permission Granted")
                    registerLocationManger()
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        requestLocationPermission()
                    } else {
                        Log.d(
                            TAG,
                            "onRequestPermissionsResult: Rationale Blocked. Need to create dialog to manually accept location permission"
                        )
                    }

                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationManger() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10.0f, this)
    }


    //******************* Nav Drawer Listener Overrides ********************************

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        Log.d(TAG, "onNavigationItemSelected: ${item.itemId}")
        when (item.itemId) {
            R.id.menu_item_restaurant -> {
                getRestaurants()
            }
            R.id.menu_item_gas_station -> {
                getGasStations()
            }
            R.id.menu_item_groceries -> {
                getGroceries()
            }
            R.id.menu_item_hotel -> {
                getHotels()
            }
            R.id.menu_item_pharmacy -> {
                getPharmacy()
            }
            R.id.menu_item_all -> {
                getAll()
            }
        }
        navDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    private fun getRestaurants() {

        var restaurantList: MutableList<PlacesResult> = mutableListOf()



        val camlat = mMap.cameraPosition.target.latitude
        val camLong = mMap.cameraPosition.target.longitude




        mapViewModel.getPlacesType("$camlat,$camLong", "$radius", "restaurant")

        mapViewModel.placeTypeLiveData.observe(this, Observer {
            mMap.clear()
            drawCurrentCircle()
            drawRadiusCircle(camlat, camLong)
            it.forEach {

                val lat = it.geometry.location.lat
                val lng = it.geometry.location.lng
                val name = it.name
                restaurantList.add(it)
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat, lng))
                        .title(name)
                )
            }
            placesAdapter.updatePlaceList(restaurantList)
            if (rvPlaces.visibility == View.GONE)
            {
                rvPlaces.visibility = View.VISIBLE
                rvPlaces.startAnimation(slide_in_bottom)
            }

        })

        lastSelected = "restaurant"

    }

    private fun getGasStations() {

        var gasStationList: MutableList<PlacesResult> = mutableListOf()

        val camlat = mMap.cameraPosition.target.latitude
        val camLong = mMap.cameraPosition.target.longitude



        mapViewModel.getPlacesType("$camlat,$camLong", "$radius", "gas_station")

        mapViewModel.placeTypeLiveData.observe(this, Observer {
            mMap.clear()
            drawCurrentCircle()
            drawRadiusCircle(camlat, camLong)
            it.forEach {
                val lat = it.geometry.location.lat
                val lng = it.geometry.location.lng
                val name = it.name
                gasStationList.add(it)
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat, lng))
                        .title(name)
                )
            }

            placesAdapter.updatePlaceList(gasStationList)
            if (rvPlaces.visibility == View.GONE)
            {
                rvPlaces.visibility = View.VISIBLE
                rvPlaces.startAnimation(slide_in_bottom)
            }

        })

        lastSelected = "gas_station"

    }

    private fun getGroceries() {

        var groceriesList: MutableList<PlacesResult> = mutableListOf()


        val camlat = mMap.cameraPosition.target.latitude
        val camLong = mMap.cameraPosition.target.longitude

        mapViewModel.getPlacesType("$camlat,$camLong", "$radius", "supermarket")

        mapViewModel.placeTypeLiveData.observe(this, Observer {
            mMap.clear()
            drawCurrentCircle()
            drawRadiusCircle(camlat, camLong)
            it.forEach {
                val lat = it.geometry.location.lat
                val lng = it.geometry.location.lng
                val name = it.name
                groceriesList.add(it)
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat, lng))
                        .title(name)
                )
            }

            placesAdapter.updatePlaceList(groceriesList)
            if (rvPlaces.visibility == View.GONE)
            {
                rvPlaces.visibility = View.VISIBLE
                rvPlaces.startAnimation(slide_in_bottom)
            }

        })

        lastSelected = "supermarket"
    }

    private fun getHotels() {

        var hotelList: MutableList<PlacesResult> = mutableListOf()


        val camlat = mMap.cameraPosition.target.latitude
        val camLong = mMap.cameraPosition.target.longitude

        mapViewModel.getPlacesType("$camlat,$camLong", "$radius", "lodging")

        mapViewModel.placeTypeLiveData.observe(this, Observer {
            mMap.clear()
            drawCurrentCircle()
            drawRadiusCircle(camlat, camLong)
            it.forEach {
                val lat = it.geometry.location.lat
                val lng = it.geometry.location.lng
                val name = it.name
                hotelList.add(it)
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat, lng))
                        .title(name)
                )
            }

            placesAdapter.updatePlaceList(hotelList)
            if (rvPlaces.visibility == View.GONE)
            {
                rvPlaces.visibility = View.VISIBLE
                rvPlaces.startAnimation(slide_in_bottom)
            }

        })
        lastSelected = "lodging"
    }

    private fun getPharmacy() {

        var pharmacyList: MutableList<PlacesResult> = mutableListOf()


        val camlat = mMap.cameraPosition.target.latitude
        val camLong = mMap.cameraPosition.target.longitude

        mapViewModel.getPlacesType("$camlat,$camLong", "$radius", "pharmacy")

        mapViewModel.placeTypeLiveData.observe(this, Observer {
            mMap.clear()
            drawCurrentCircle()
            drawRadiusCircle(camlat, camLong)
            it.forEach {
                val lat = it.geometry.location.lat
                val lng = it.geometry.location.lng
                val name = it.name
                pharmacyList.add(it)
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat, lng))
                        .title(name)
                )
            }

            placesAdapter.updatePlaceList(pharmacyList)
            if (rvPlaces.visibility == View.GONE)
            {
                rvPlaces.visibility = View.VISIBLE
                rvPlaces.startAnimation(slide_in_bottom)
            }

        })
        lastSelected = "pharmacy"
    }

    private fun getAll() {

        var allList: MutableList<PlacesResult> = mutableListOf()

        val camlat = mMap.cameraPosition.target.latitude
        val camLong = mMap.cameraPosition.target.longitude

        mapViewModel.getPlaces("$camlat,$camLong", "$radius")

        mapViewModel.placesLiveData.observe(this, Observer {
            mMap.clear()
            drawCurrentCircle()
            drawRadiusCircle(camlat, camLong)
            it.forEach {
                val lat = it.geometry.location.lat
                val lng = it.geometry.location.lng
                val name = it.name
                allList.add(it)
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat, lng))
                        .title(name)
                )
            }

            placesAdapter.updatePlaceList(allList)
            if (rvPlaces.visibility == View.GONE)
            {
                rvPlaces.visibility = View.VISIBLE
                rvPlaces.startAnimation(slide_in_bottom)
            }

        })
        lastSelected = ""
    }
    //**********************************************************************************


    /*
    *
    * SeekBar Change Listener Implementation
    *
    * */

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        seekBarTextView.text = "$p1 Meters"
        radius = p1
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

        var allList: MutableList<PlacesResult> = mutableListOf()
        val lat = mMap.cameraPosition.target.latitude
        val long = mMap.cameraPosition.target.longitude

        mMap.clear()
        drawRadiusCircle(lat, long)
        drawCurrentCircle()


        if (lastSelected == "") {
            mapViewModel.getPlaces("$lat,$long", "$radius")
        } else {
            mapViewModel.getPlacesType("$lat,$long", "$radius", "$lastSelected")
        }
        mapViewModel.placesLiveData.observe(this, Observer {
            mMap.clear()
            drawCurrentCircle()
            drawRadiusCircle(lat, long)
            it.forEach {
                val lat = it.geometry.location.lat
                val lng = it.geometry.location.lng
                val name = it.name
                allList.add(it)
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat, lng))
                        .title(name)
                )
            }

        })
    }


}