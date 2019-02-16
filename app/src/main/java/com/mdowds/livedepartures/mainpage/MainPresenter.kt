package com.mdowds.livedepartures.mainpage

import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.wear.ambient.AmbientModeSupport
import android.util.Log
import com.android.volley.ClientError
import com.mdowds.livedepartures.ArrivalsDataSource
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.NearbyStopPointsDataSource
import com.mdowds.livedepartures.networking.model.TflStopPoint
import com.mdowds.livedepartures.networking.model.TflStopPoints

class MainPresenter(private val view: MainView,
                    private val stopPointsDataSource: NearbyStopPointsDataSource,
                    private val arrivalsDataSource: ArrivalsDataSource):
        ViewPager.OnPageChangeListener,
        AmbientModeSupport.AmbientCallback() {

    var modes = listOf<Mode>()
        private set

    var stopPoints = listOf<TflStopPoint>()
        private set

    private var currentPosition = 0

    init {
        stopPointsDataSource.addObserver(this::stopPointsUpdated, this::stopPointsError)
    }

    //region lifecycle

    fun onResume() {
        Log.d("MainPresenter", "Resuming")
        stopPointsDataSource.startUpdates()
        arrivalsDataSource.startUpdates()
    }

    fun onPause() {
        Log.d("MainPresenter", "Pausing")
        stopPointsDataSource.stopUpdates()
        arrivalsDataSource.stopUpdates()
    }

    //endregion

    //region event handlers

    fun onClickRetry() {
        view.showLoadingSpinner()
        stopPointsDataSource.requestNearbyStops()
    }

    //endregion

    //region OnPageChangeListener implementation

    override fun onPageSelected(position: Int) {
        Log.d("MainPresenter", "Change page to $position")
        currentPosition = position
        setHeaderColors()
    }

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    //endregion

    //region AmbientCallback implementation

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        Log.d("MainPresenter", "Entering ambient mode")
        super.onEnterAmbient(ambientDetails)
        view.setHeaderBackgroundColor(Color.BLACK)
        view.setHeaderTextColor(Color.WHITE)
        stopPointsDataSource.stopUpdates()
        arrivalsDataSource.stopUpdates()
    }

    override fun onExitAmbient() {
        Log.d("MainPresenter", "Exiting ambient mode")
        super.onExitAmbient()
        stopPointsDataSource.startUpdates()
        arrivalsDataSource.startUpdates()
        if(modes.count() > 0) setHeaderColors()
    }

    override fun onUpdateAmbient() {
        Log.d("MainPresenter", "Ambient mode update")
        super.onUpdateAmbient()
        arrivalsDataSource.requestArrivalsForAll()
    }

    //endregion

    //region StopPointsDataSource observer

    fun stopPointsUpdated(newStopPoints: TflStopPoints) {
        Log.d("MainPresenter", "New stop points received")

        if(newStopPoints.places.isEmpty()) return view.showNoStopsMessage()

        view.showLoadingSpinner()

        stopPoints = newStopPoints.places
        arrivalsDataSource.removeStopPoints()

        modes = newStopPoints.places
                .flatMap { it.modes }
                .asSequence()
                .distinct()
                .map { Mode.fromModeName(it) }
                .filterNotNull()
                .sorted()
                .toList()

        view.refreshStopPoints()
        view.showDeparturesPages()
    }

    private fun stopPointsError(error: Exception) {
        when(error) {
            is ClientError -> view.showNoStopsMessage()
            else -> view.showRetryMessage()
        }
    }

    //endregion

    private fun setHeaderColors() {
        val mode = modes[currentPosition]

        if (mode.color == Color.WHITE) {
            view.setHeaderTextColor(Color.BLACK)
        } else {
            view.setHeaderTextColor(Color.WHITE)
        }

        view.setHeaderBackgroundColor(mode.color)
    }

}