package com.mdowds.livedepartures

import com.mdowds.livedepartures.networking.*
import io.github.luizgrp.sectionedrecyclerviewadapter.Section

class ArrivalsPresenter(private val view: ArrivalsView,
                        private val locationManager: LocationManager,
                        private val api: TransportInfoApi) {

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
        locationManager.startLocationUpdates {
            api.getNearbyStops(it.latitude, it.longitude, this::onStopPointsResponse)
        }
    }

    fun onPause() {
        locationManager.stopLocationUpdates()
    }

    fun onStopPointsResponse(stopPoints: TflStopPoints) {
        stopPoints.places.take(5).forEach {
            val newSection = view.addStopSection(it)
            requestArrivals(it, newSection)
        }
    }

    private fun requestArrivals(stopPoint: TflStopPoint, section: Section) {
        api.getArrivals(stopPoint) {
            onArrivalsResponse(it, section)
        }
    }

    fun onArrivalsResponse(newResults: List<TflArrivalPrediction>, section: Section) {
        val newResultsOrdered = newResults.sortedBy { it.timeToStation }
        val newArrivals = newResultsOrdered.take(5).map { ArrivalModel(it) }
        view.updateResults(newArrivals, section)
    }
}