package com.mdowds.livedepartures

import android.location.Location
import android.util.Log
import com.mdowds.livedepartures.mainpage.MainActivity
import com.mdowds.livedepartures.networking.*
import com.mdowds.livedepartures.utils.*
import com.mdowds.livedepartures.utils.Observable

class NearbyStopPointsDataSource(private val config: Config,
                                 private val locationManager: LocationManager,
                                 private val api: TransportInfoApi) : Observable<TflStopPoints?>() {

    companion object {
        fun create(view: MainActivity): NearbyStopPointsDataSource {

            val config = AppConfig(view.resources).config
            val locationManager = FusedLocationManager(view)

            return NearbyStopPointsDataSource(config, locationManager, TflApi(RequestQueueSingleton.getInstance(view.applicationContext).requestQueue))
        }
    }

    private var currentLocation: Location? = null

    fun startUpdates() = locationManager.startLocationUpdates(this::onLocationResponse)

    fun stopUpdates() = locationManager.stopLocationUpdates()

    fun onLocationResponse(location: Location) {
        if (!locationHasSignificantlyChanged(currentLocation, location)) return

        currentLocation = location
        requestNearbyStops()
    }

    fun requestNearbyStops() {
        val location = currentLocation ?: return

        api.getNearbyStops(location.latitude,
                location.longitude,
                config.radiusToFetchStopsInMetres,
                this::onStopPointsResponse) { notifyObservers(null) }
    }

    private fun onStopPointsResponse(stopPoints: TflStopPoints) = notifyObservers(stopPoints)

    private fun locationHasSignificantlyChanged(currentLocation: Location?, newLocation: Location): Boolean {
        currentLocation ?: return true
        return currentLocation.distanceTo(newLocation) > config.distanceToFetchNewStopsInMetres
    }
}
