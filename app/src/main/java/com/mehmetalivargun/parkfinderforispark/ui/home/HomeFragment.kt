package com.mehmetalivargun.parkfinderforispark.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mehmetalivargun.parkfinderforispark.R
import com.mehmetalivargun.parkfinderforispark.base.BaseFragment
import com.mehmetalivargun.parkfinderforispark.databinding.FragmentHomeBinding
import com.mehmetalivargun.parkfinderforispark.util.resolveToFee
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var bottomSheet: LinearLayout
    private lateinit var behavior: BottomSheetBehavior<LinearLayout>
    private val random = Random()

    companion object {
        private const val LOCATION_REQUEST_CODE = 1

    }

    override fun FragmentHomeBinding.initialize() {
        this@HomeFragment.bottomSheet = binding.bottomSheet
        behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.determinateBar.getProgressDrawable().setColorFilter(
            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN
        )

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this@HomeFragment)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.mapstyle
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
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                val sydney = LatLng(41.023, 28.952)
                viewModel.parks.observe(this, {
                    it.forEach {
                        val locat = LatLng(it.lat.toDouble(), it.lng.toDouble())
                        placeMarkerOnMap(locat, it.parkID, false, it.parkName)
                    }
                })
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 13f))

            }
        }
    }


    private fun placeMarkerOnMap(
        currentLatLong: LatLng,
        parkID: Int,
        base: Boolean = true,
        name: String
    ) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        when (base) {
            true -> markerOptions.title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(random.nextFloat() * 360))
            false -> markerOptions.title(name)
        }
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

                    oneHourPrice.text = tariff[1]
                    twoHourPrice.text = tariff[3]
                    fourHourPrice.text = tariff[5]
                    eightHourPrice.text = tariff[7]
                    allDayPrice.text = tariff[9]
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


}