package com.mdowds.livedepartures

import android.app.Activity
import android.location.Location
import com.google.android.gms.location.*

typealias LastLocationCallback = (Location) -> Unit

class LocationManager(private val fusedLocationClient : FusedLocationProviderClient) {

    constructor(requestingActivity: Activity) : this(LocationServices.getFusedLocationProviderClient(requestingActivity))

    private val locationCallback = object : LocationCallback() {

        var lastLocationCallback: LastLocationCallback = {}

        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            lastLocationCallback(locationResult.lastLocation)
        }
    }

    fun startLocationUpdates(callback: LastLocationCallback) {

        locationCallback.lastLocationCallback = callback

        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */)
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}