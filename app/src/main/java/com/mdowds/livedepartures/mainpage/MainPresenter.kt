package com.mdowds.livedepartures.mainpage

import android.graphics.Color
import android.support.v4.view.ViewPager
import com.mdowds.livedepartures.ArrivalsDataSource
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.NearbyStopPointsDataSource
import com.mdowds.livedepartures.networking.TflStopPoint
import com.mdowds.livedepartures.networking.TflStopPoints

class MainPresenter(private val view: MainView,
                    private val stopPointsDataSource: NearbyStopPointsDataSource,
                    private val arrivalsDataSource: ArrivalsDataSource): ViewPager.OnPageChangeListener {

    var modes = listOf<Mode>()
        private set

    var stopPoints = listOf<TflStopPoint>()
        private set

    init {
        stopPointsDataSource.addObserver(this::stopPointsUpdated)
    }

    fun onResume() {
        stopPointsDataSource.startUpdates()
        arrivalsDataSource.startUpdates()
    }

    fun onPause() {
        stopPointsDataSource.stopUpdates()
        arrivalsDataSource.stopUpdates()
    }

    fun onClickRetry() {
        view.showLoadingSpinner()
        stopPointsDataSource.requestNearbyStops()
    }

    override fun onPageSelected(position: Int) {
        val mode = modes[position]

        if (mode.color == Color.WHITE) {
            view.setHeaderTextColor(Color.BLACK)
        } else {
            view.setHeaderTextColor(Color.WHITE)
        }

        view.setHeaderBackgroundColor(mode.color)
    }

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    fun stopPointsUpdated(newStopPoints: TflStopPoints?) {

        if(newStopPoints == null) return view.showRetryMessage()
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
}