package com.mdowds.livedepartures

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.util.Log
import com.mdowds.livedepartures.networking.*
import com.mdowds.livedepartures.utils.*
import com.mdowds.livedepartures.utils.DevicePermissionsManager.Companion.PERMISSIONS_REQUEST_CODE
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import java.util.*

class ArrivalsPresenter(private val view: ArrivalsView,
                        private val config: Config,
                        private val locationManager: LocationManager,
                        private val api: TransportInfoApi,
                        private val arrivalRequestsTimer: Timer) {

    private var currentLocation: Location? = null

    companion object {
        fun create(view: ArrivalsActivity): ArrivalsPresenter {

            val config = AppConfig(view.resources).config
            val locationManager = if (config.useFakeLocation) FakeLocationManager(config.fakeLocation)
                else FusedLocationManager(view)

            return ArrivalsPresenter(view,
                    config,
                    locationManager,
                    TflApi(RequestQueueSingleton.getInstance(view.applicationContext).requestQueue),
                    Timer("Arrival requests")
            )
        }
    }

    fun onResume() {
        if (currentLocation == null) view.showLoadingSpinner()
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
        if (!locationHasSignificantlyChanged(currentLocation, location)) return

        currentLocation = location
        api.getNearbyStops(location.latitude, location.longitude, this::onStopPointsResponse)
    }

    fun onStopPointsResponse(stopPoints: TflStopPoints) {
        view.removeStopSections()
        arrivalRequestsTimer.purge()

        stopPoints.places
                .filter { it.lines.isNotEmpty() }
                .take(config.stopsToShow)
                .forEach { tflStopPoint ->
                    val newSection = view.addStopSection(StopPoint(tflStopPoint))
                    startArrivalsUpdates(tflStopPoint, newSection)
                }

        view.hideLoadingSpinner()
    }

    fun onArrivalsResponse(newResults: List<TflArrivalPrediction>, section: Section, updateArrivalsTask: TimerTask) {
        val newResultsOrdered = newResults.sortedBy { it.timeToStation }
        val newArrivals = newResultsOrdered.take(config.departuresPerStop).map { Arrival(it) }
        view.updateResults(newArrivals, section)

        if (newArrivals.isEmpty()) updateArrivalsTask.cancel()
    }

    private fun startLocationUpdates() = locationManager.startLocationUpdates(this::onLocationResponse)

    private fun locationHasSignificantlyChanged(currentLocation: Location?, newLocation: Location): Boolean {
        currentLocation ?: return true
        return currentLocation.distanceTo(newLocation) > config.distanceToFetchNewStopsInMetres
    }

    private fun startArrivalsUpdates(tflStopPoint: TflStopPoint, stopSection: StopSection) {
        val repeatedTask = object : TimerTask() {
            override fun run() = requestArrivals(tflStopPoint, stopSection, this)
        }

        val period = (config.arrivalsRefreshInSecs * 1000).toLong()
        arrivalRequestsTimer.scheduleAtFixedRate(repeatedTask, 0L, period)
    }

    private fun requestArrivals(stopPoint: TflStopPoint, section: Section, updateArrivalsTask: TimerTask) {
        api.getArrivals(stopPoint) {
            onArrivalsResponse(it, section, updateArrivalsTask)
        }
    }
}
