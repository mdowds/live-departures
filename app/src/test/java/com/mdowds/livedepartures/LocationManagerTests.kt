package com.mdowds.livedepartures

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Test

class LocationManagerTests {

    @Test
    fun `startLocationUpdates calls location client with correct request`() {
        val locationClient = mock<FusedLocationProviderClient>()
        val locationManager = LocationManager(locationClient)

        locationManager.startLocationUpdates {}

        verify(locationClient).requestLocationUpdates(check {
            assertEquals(10000L, it.interval)
            assertEquals(5000L, it.fastestInterval)
            assertEquals(LocationRequest.PRIORITY_HIGH_ACCURACY, it.priority)
        }, any(), anyOrNull())
    }
}