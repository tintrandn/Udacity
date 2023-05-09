package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.*
import java.util.Locale

class RepresentativeFragment : Fragment() {

    companion object {
        private const val ACCESS_FINE_LOCATION_REQUEST_CODE = 21
    }

    private val viewModel: RepresentativeViewModel by viewModels()

    private val locationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRepresentativeBinding
            .inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.representativesRecycler.adapter = RepresentativeListAdapter()

        binding.buttonLocation.setOnClickListener {
            getUserLocation()
        }

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.address.value?.state = binding.state.selectedItem.toString()
            viewModel.fetchRepresentatives()
        }

        viewModel.error.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        return binding.root

    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        locationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    setAddressAndGetRepresentatives(geoCodeLocation(location))
                }
            }
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
                ACCESS_FINE_LOCATION_REQUEST_CODE
            )
        }
    }

    private fun requestLocationPermission() {
        this.requestPermissions(
            arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
            ACCESS_FINE_LOCATION_REQUEST_CODE
        )
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun setAddressAndGetRepresentatives(address: Address) {
        viewModel.setCurrentAddress(address)
        viewModel.fetchRepresentatives()
    }
}