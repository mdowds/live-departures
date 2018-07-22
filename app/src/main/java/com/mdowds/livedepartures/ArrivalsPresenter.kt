package com.mdowds.livedepartures

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.util.Log
import com.mdowds.livedepartures.networking.*
import com.mdowds.livedepartures.utils.DevicePermissionsManager.Companion.PERMISSIONS_REQUEST_CODE
import com.mdowds.livedepartures.utils.FusedLocationManager
import com.mdowds.livedepartures.utils.LocationManager
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import java.util.*

class ArrivalsPresenter(private val view: ArrivalsView,
                        private val locationManager: LocationManager,
                        private val api: TransportInfoApi,
                        private val arrivalRequestsTimer: Timer) {

    private var currentLocation: Location? = null

    companion object {
        fun create(view: ArrivalsActivity): ArrivalsPresenter {
            return ArrivalsPresenter(view,
                    FusedLocationManager(view),
                    TflApi(RequestQueueSingleton.getInstance(view.applicationContext).requestQueue),
                    Timer("Arrival requests")
            )
        }
    }

    fun onResume() {
        // TODO overall loading state for location and stops fetches
        startLocationUpdates()
    }

    fun onPause() {
        locationManager.stopLocationUpdates()
        arrivalRequestsTimer.purge()
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Log.i("Permissions result", "User interaction was cancelled.")
                (grantResults[0] == PERMISSION_GRANTED) -> startLocationUpdates()
                else -> {
                    // TODO show message with link to settings if permission rejected
//                    showSnackbar(R.string.permission_denied_explanation, R.string.settings,
//                            View.OnClickListener {
//                                // Build intent that displays the App settings screen.
//                                val intent = Intent().apply {
//                                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                                    data = Uri.fromParts("package", APPLICATION_ID, null)
//                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                }
//                                startActivity(intent)
//                            })
                }
            }
        }
    }

    fun onLocationResponse(location: Location) {
        if(!locationHasSignificantlyChanged(currentLocation, location)) return

        currentLocation = location
        api.getNearbyStops(location.latitude, location.longitude, this::onStopPointsResponse)
    }

    fun onStopPointsResponse(stopPoints: TflStopPoints) {
        view.removeStopSections()
        arrivalRequestsTimer.purge()

        stopPoints.places.take(5).forEach {
            val newSection = view.addStopSection(it)

            val repeatedTask = object : TimerTask() {
                override fun run() = requestArrivals(it, newSection)
            }

            val delay = 0L
            val period = 10000L
            arrivalRequestsTimer.scheduleAtFixedRate(repeatedTask, delay, period)
        }
    }

    fun onArrivalsResponse(newResults: List<TflArrivalPrediction>, section: Section) {
        val newResultsOrdered = newResults.sortedBy { it.timeToStation }
        val newArrivals = newResultsOrdered.take(5).map { ArrivalModel(it) }
        view.updateResults(newArrivals, section)
    }

    private fun startLocationUpdates() = locationManager.startLocationUpdates(this::onLocationResponse)

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
