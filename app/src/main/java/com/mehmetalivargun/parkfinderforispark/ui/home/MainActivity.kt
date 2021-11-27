package com.mehmetalivargun.parkfinderforispark.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint


import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mehmetalivargun.parkfinderforispark.R
import com.mehmetalivargun.parkfinderforispark.databinding.ActivityMainBinding
import com.mehmetalivargun.parkfinderforispark.util.resolveToFee
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, OnMapsSdkInitializedCallback {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var bottomSheet: LinearLayout
    private lateinit var behavior: BottomSheetBehavior<LinearLayout>
    private val random = Random()

    companion object {
        private const val LOCATION_REQUEST_CODE = 1

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        this.bottomSheet = binding.bottomSheet
        behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.determinateBar.getProgressDrawable().setColorFilter(
            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN
        )
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.mapstyle
                )
            )
            if (!success) {
                Log.e("TAG", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", e)
        }
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        setupMap()
    }

    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                val loc = Location("")
                loc.latitude = location.latitude
                loc.longitude = location.longitude


                val sydney = LatLng(41.023, 28.952)
                viewModel.parks.observe(this, {
                    it.forEach {
                        if (it.isOpen==1){
                            val locat = LatLng(it.lat.toDouble(), it.lng.toDouble())
                            placeMarkerOnMap(locat, it.parkID,  it.parkName)
                        }

                    }
                })
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 13.2f))
            }
        }
    }

    private fun placeMarkerOnMap(
        currentLatLong: LatLng,
        parkID: Int,
        name: String
    ) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title(name)
        map.addMarker(markerOptions)?.tag = parkID
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        viewModel.getDetail(p0.tag.toString().toInt())
        var name = ""
        viewModel.parkDetail.observe(this, {
            it.forEach {
                binding.apply {
                    name = it.parkName
                    parkName.text = it.parkName
                    adressText.text = it.address
                    determinateBar.progress =
                        ((100.0 / it.capacity) * (it.capacity - it.emptyCapacity)).toInt()
                    val tariff = it.tariff.resolveToFee()
                    tariffText.text = tariff
                    workTime.text="${it.workHours} açık"
                    emptyCapacity.text="Boş Alan :${it.emptyCapacity}"

                    districtName.text = it.district
                }
            }
        })
        binding.apply {
            fabButtonMap.visibility = View.VISIBLE
            fabButtonMap.setOnClickListener {
                val gmmIntentUri =
                    Uri.parse("google.navigation:q=${p0.position.latitude},${p0.position.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.determinateBar.progress = 12
        return false
    }

    override fun onMapsSdkInitialized(p0: MapsInitializer.Renderer) {
        when (p0) {
            MapsInitializer.Renderer.LATEST -> Log.d(
                "MapsDemo",
                "The latest version of the renderer is used."
            )
            MapsInitializer.Renderer.LEGACY -> Log.d(
                "MapsDemo",
                "The legacy version of the renderer is used."
            )
        }
    }


}