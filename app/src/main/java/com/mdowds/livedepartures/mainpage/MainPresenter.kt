package com.mdowds.livedepartures.mainpage

import android.graphics.Color
import android.support.v4.view.ViewPager
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.NearbyStopPointsDataSource
import com.mdowds.livedepartures.networking.TflStopPoints

class MainPresenter(private val view: MainView,
                    private val dataSource: NearbyStopPointsDataSource): ViewPager.OnPageChangeListener {

    var modes = listOf<Mode>()
        private set

    init {
        dataSource.addObserver(this::stopPointsUpdated)
    }

    fun onResume() {
        dataSource.startUpdates()
    }

    fun onPause() {
        dataSource.stopUpdates()
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

    fun stopPointsUpdated(stopPoints: TflStopPoints) {
        view.hideLoadingSpinner()

        modes = stopPoints.places
                .flatMap { it.modes }
                .asSequence()
                .distinct()
                .map { Mode.fromModeName(it) }
                .filterNotNull()
                .toList()

        view.updateModes(modes)
    }
}