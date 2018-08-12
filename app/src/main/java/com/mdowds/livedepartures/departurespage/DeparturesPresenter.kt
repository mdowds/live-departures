package com.mdowds.livedepartures.departurespage

import com.mdowds.livedepartures.Departure
import com.mdowds.livedepartures.NearbyStopPointsDataSource
import com.mdowds.livedepartures.StopPoint
import com.mdowds.livedepartures.networking.*
import com.mdowds.livedepartures.utils.AppConfig
import com.mdowds.livedepartures.utils.Config
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import java.util.*

class DeparturesPresenter(private val view: DeparturesView,
                          private val config: Config,
                          private val api: TransportInfoApi,
                          private val arrivalRequestsTimer: Timer,
                          private val stopPointsDataSource: NearbyStopPointsDataSource) {

    companion object {
        fun create(view: DeparturesFragment, stopPointsDataSource: NearbyStopPointsDataSource): DeparturesPresenter {

            val config = AppConfig(view.resources).config

            return DeparturesPresenter(view,
                    config,
                    TflApi(RequestQueueSingleton.getInstance(view.activity!!.applicationContext).requestQueue),
                    Timer("Departure requests"),
                    stopPointsDataSource
            )
        }
    }

    init {
        stopPointsDataSource.addObserver(this::updateStopPoints)
        val stopPoints = stopPointsDataSource.currentStopPoints
        if(stopPoints != null) updateStopPoints(stopPoints)
    }

    fun onResume() {
        if (stopPointsDataSource.currentStopPoints == null) view.showLoadingSpinner()
    }

    fun onPause() {
        stopArrivalsUpdates()
    }

    fun onStop() = stopPointsDataSource.removeObserver(this::updateStopPoints)

    fun onArrivalsResponse(newResults: List<TflArrivalPrediction>, section: Section, updateArrivalsTask: TimerTask) {
        val newResultsOrdered = newResults.sortedBy { it.timeToStation }
        val newDepartures = newResultsOrdered.take(config.departuresPerStop).map { Departure(it) }
        view.updateResults(newDepartures, section)

        if (newDepartures.isEmpty()) updateArrivalsTask.cancel()
    }

    fun updateStopPoints(tflStopPoints: TflStopPoints) {

        stopArrivalsUpdates()
        view.removeStopSections()

        // TODO pass in the relevant mode on creation and filter out here
        tflStopPoints.places
                .filter { it.lines.isNotEmpty() }
                .take(config.stopsToShow)
                .forEach { tflStopPoint ->
                    val newSection = view.addStopSection(StopPoint(tflStopPoint))
                    startArrivalsUpdates(tflStopPoint, newSection)
                }

        view.hideLoadingSpinner()
    }

    private fun startArrivalsUpdates(tflStopPoint: TflStopPoint, stopSection: StopSection) {
        val repeatedTask = object : TimerTask() {
            override fun run() = requestArrivals(tflStopPoint, stopSection, this)
        }

        val period = (config.departuresRefreshInSecs * 1000).toLong()
        arrivalRequestsTimer.scheduleAtFixedRate(repeatedTask, 0L, period)
    }

    private fun stopArrivalsUpdates() = arrivalRequestsTimer.purge()

    private fun requestArrivals(stopPoint: TflStopPoint, section: Section, updateArrivalsTask: TimerTask) {
        api.getArrivals(stopPoint) {
            onArrivalsResponse(it, section, updateArrivalsTask)
        }
    }
}
