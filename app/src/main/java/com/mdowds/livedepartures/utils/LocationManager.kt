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
                           private val permissionsManager: PermissionsManager) : LocationManager {

    constructor(requestingActivity: Activity) : this(
            LocationServices.getFusedLocationProviderClient(requestingActivity), requestingActivity, DevicePermissionsManager()
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

            locationCallback.lastLocationCallback = callback

            val locationRequest = LocationRequest().apply {
                interval = 10000
                fastestInterval = 5000
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


class FakeLocationManager: LocationManager {

    private val locationToReturn = Location("").apply {
        val asStrings = "51.583899, -0.020362"
                .replace("\\s".toRegex(), "")
                .split(",")

        latitude = asStrings[0].toDouble()
        longitude = asStrings[1].toDouble()
    }

    override fun startLocationUpdates(callback: LastLocationCallback) {
        callback(locationToReturn)
    }

    override fun stopLocationUpdates() {}
}

