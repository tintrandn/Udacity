package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import kotlinx.android.synthetic.main.fragment_select_location.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

private const val TAG = "SelectLocationFragment"
private const val FINE_LOCATION_PERMISSION_REQUEST_CODE = 1

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by sharedViewModel()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var marker: Marker

    private val locationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.saveBtn.setOnClickListener {
                onLocationSelected()
             }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            getUserLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        // add style to the map
        setMapStyle()
        // put a marker to location that the user selected
        setMapLongClick()
        setPoiClick()
        // check if grant ACCESS_FINE_LOCATION permission first
        if (isPermissionGranted()) {
            // zoom to the user location, the permission get before at save reminder
            getUserLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun onLocationSelected() {
        if (this::marker.isInitialized) {
            _viewModel.latitude.set(marker.position.latitude)
            _viewModel.longitude.set(marker.position.longitude)
            _viewModel.reminderSelectedLocationStr.set(marker.title)
            _viewModel.navigationCommand.value = NavigationCommand.Back
        }
    }

    private fun setMapStyle() {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun setMapLongClick() {
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            )

            marker.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    private fun setPoiClick() {
        map.setOnPoiClickListener {
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(it.latLng)
                    .title(it.name)
            )
            marker.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLng(it.latLng))
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        map.isMyLocationEnabled = true
        Log.d("MapsActivity", "getLastLocation Called")
        locationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
                    marker = map.addMarker(
                        MarkerOptions()
                            .position(userLocation)
                            .title(getString(R.string.my_location))
                            .icon(BitmapDescriptorFactory.defaultMarker())
                    )
                    marker.showInfoWindow()
                }
            }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            this.requestPermissions(
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
}
