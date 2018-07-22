package com.mdowds.livedepartures

import android.location.Location
import com.mdowds.livedepartures.networking.*
import io.github.luizgrp.sectionedrecyclerviewadapter.Section

class ArrivalsPresenter(private val view: ArrivalsView,
                        private val locationManager: LocationManager,
                        private val api: TransportInfoApi) {

    private var currentLocation: Location? = null

    companion object {
        fun create(view: ArrivalsActivity): ArrivalsPresenter {
            return ArrivalsPresenter(view,
                    FusedLocationManager(view),
                    TflApi(RequestQueueSingleton.getInstance(view.applicationContext).requestQueue)
            )
        }
    }

    fun onResume() {
        // TODO overall loading state for location and stops fetches
        locationManager.startLocationUpdates(this::onLocationResponse)
    }

    fun onPause() {
        locationManager.stopLocationUpdates()
    }

    fun onLocationResponse(location: Location) {
        if(!locationHasSignificantlyChanged(currentLocation, location)) return

        currentLocation = location
        api.getNearbyStops(location.latitude, location.longitude, this::onStopPointsResponse)
    }

    fun onStopPointsResponse(stopPoints: TflStopPoints) {
        stopPoints.places.take(5).forEach {
            val newSection = view.addStopSection(it)
            requestArrivals(it, newSection)
        }
    }

    fun onArrivalsResponse(newResults: List<TflArrivalPrediction>, section: Section) {
        val newResultsOrdered = newResults.sortedBy { it.timeToStation }
        val newArrivals = newResultsOrdered.take(5).map { ArrivalModel(it) }
        view.updateResults(newArrivals, section)
    }

    private fun locationHasSignificantlyChanged(currentLocation: Location? ,newLocation: Location) : Boolean {
        currentLocation ?: return true
        return currentLocation.distanceTo(newLocation) > 10
    }

    private fun requestArrivals(stopPoint: TflStopPoint, section: Section) {
        api.getArrivals(stopPoint) {
            onArrivalsResponse(it, section)
        }
    }
}