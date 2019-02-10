package com.mdowds.livedepartures.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import com.google.android.gms.location.*

typealias LastLocationCallback = (Location) -> Unit

interface LocationManager {
    fun startLocationUpdates(callback: LastLocationCallback)
    fun stopLocationUpdates()
}

class FusedLocationManager(private val fusedLocationClient: FusedLocationProviderClient,
                           private val requestingActivity: Activity,
                           private val permissionsManager: PermissionsManager,
                           private val config: Config) : LocationManager {

    constructor(requestingActivity: Activity, config: Config) : this(
            LocationServices.getFusedLocationProviderClient(requestingActivity),
            requestingActivity,
            DevicePermissionsManager(),
            config
    )

    private val locationCallback = object : LocationCallback() {

        var lastLocationCallback: LastLocationCallback = {}

        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            lastLocationCallback(locationResult.lastLocation)
        }
    }

    @SuppressLint("MissingPermission")
    override fun startLocationUpdates(callback: LastLocationCallback) {
        if (permissionsManager.isPermissionGranted(requestingActivity, ACCESS_FINE_LOCATION)) {

            // Send last known location first in case the watch can't get an updated one from the phone
            fusedLocationClient.lastLocation.addOnSuccessListener(callback)

            locationCallback.lastLocationCallback = callback

            val refreshInterval = config.distanceToFetchNewStopsInMetres * 1000L
            val locationRequest = LocationRequest().apply {
                interval = refreshInterval
                fastestInterval = refreshInterval / 2
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null /* Looper */)
        } else {
            permissionsManager.requestPermissions(requestingActivity, ACCESS_FINE_LOCATION)
        }
    }

    override fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
