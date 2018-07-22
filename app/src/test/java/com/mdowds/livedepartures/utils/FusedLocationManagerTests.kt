package com.mdowds.livedepartures.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.mdowds.livedepartures.helpers.TestDataFactory.makeLocation
import com.mdowds.livedepartures.helpers.TestDataFactory.makeLocationResult
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Test

class FusedLocationManagerTests {

    private val mockLocationClient = mock<FusedLocationProviderClient>()
    private val mockActivity = mock<Activity>()
    private val mockPermissionsManager = mock<PermissionsManager> {
        on { isPermissionGranted(any(), any()) }.thenReturn(true)
    }
    private val locationManager = FusedLocationManager(mockLocationClient, mockActivity, mockPermissionsManager)

    @Test
    fun `startLocationUpdates calls location client with correct request`() {
        locationManager.startLocationUpdates {}

        verify(mockLocationClient).requestLocationUpdates(check {
            assertEquals(10000L, it.interval)
            assertEquals(5000L, it.fastestInterval)
            assertEquals(LocationRequest.PRIORITY_HIGH_ACCURACY, it.priority)
        }, any(), anyOrNull())
    }

    @Test
    fun `startLocationUpdates calls the callback upon response from location client`() {
        val mockCallback = mock<LastLocationCallback>()

        locationManager.startLocationUpdates(mockCallback)

        argumentCaptor<LocationCallback>().apply {
            verify(mockLocationClient).requestLocationUpdates(any(), capture(), anyOrNull())

            firstValue.onLocationResult(makeLocationResult())
            verify(mockCallback).invoke(any())
        }
    }

    @Test
    fun `startLocationUpdates calls the callback with the latest location`() {
        val mockCallback = mock<LastLocationCallback>()

        locationManager.startLocationUpdates(mockCallback)

        argumentCaptor<LocationCallback>().apply {
            verify(mockLocationClient).requestLocationUpdates(any(), capture(), anyOrNull())

            firstValue.onLocationResult(LocationResult.create(listOf(
                    makeLocation(),
                    makeLocation(2.0, -2.0)
            )))
            verify(mockCallback).invoke(check {
                assertEquals(2.0, it.latitude, 0.0)
                assertEquals(-2.0, it.longitude, 0.0)
            })
        }
    }

    @Test
    fun `startLocationUpdates requests the location permission if it's not granted`() {
        whenever(mockPermissionsManager.isPermissionGranted(any(), any())).thenReturn(false)

        locationManager.startLocationUpdates{}

        verify(mockPermissionsManager).requestPermissions(any(), eq(ACCESS_FINE_LOCATION))
    }
}