package com.mdowds.livedepartures.departurespage

import com.mdowds.livedepartures.Departure
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.NearbyStopPointsDataSource
import com.mdowds.livedepartures.StopPoint
import com.mdowds.livedepartures.networking.*
import com.mdowds.livedepartures.utils.AppConfig
import com.mdowds.livedepartures.utils.Config
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import java.util.*

class DeparturesPresenter(private val view: DeparturesView,
                          private val mode: Mode?,
                          private val config: Config,
                          private val api: TransportInfoApi,
                          private val arrivalRequestsTimer: Timer,
                          private val stopPointsDataSource: NearbyStopPointsDataSource) {

    companion object {
        fun create(view: DeparturesFragment, stopPointsDataSource: NearbyStopPointsDataSource): DeparturesPresenter {

            val config = AppConfig(view.resources).config

            return DeparturesPresenter(view,
                    view.mode,
                    config,
                    TflApi(RequestQueueSingleton.getInstance(view.activity!!.applicationContext).requestQueue),
                    Timer("Departure requests"),
                    stopPointsDataSource
            )
        }
    }

    init {
        stopPointsDataSource.addObserver(this::onStopPointsUpdated)
        val stopPoints = stopPointsDataSource.currentStopPoints
        if(stopPoints != null) onStopPointsUpdated(stopPoints)
    }

    fun onResume() {
        if (stopPointsDataSource.currentStopPoints == null) view.showLoadingSpinner()
    }

    fun onStop() {
        stopArrivalsUpdates()
        stopPointsDataSource.removeObserver(this::onStopPointsUpdated)
    }

    fun onArrivalsResponse(newResults: List<TflArrivalPrediction>, section: Section, updateArrivalsTask: TimerTask) {
        val newResultsOrdered = newResults.sortedBy { it.timeToStation }
        val newDepartures = newResultsOrdered
                .asSequence()
                .filter { mode == null || it.modeName == mode.tflName }
                .take(config.departuresPerStop)
                .map { Departure(it) }
                .toList()
        view.updateResults(newDepartures, section)

        if (newDepartures.isEmpty()) updateArrivalsTask.cancel()
    }

    fun onStopPointsUpdated(tflStopPoints: TflStopPoints) {

        stopArrivalsUpdates()
        view.removeStopSections()

        tflStopPoints.places
                .asSequence()
                .filter { it.lines.isNotEmpty() }
                .filter { mode == null || it.modes.contains(mode.tflName) }
                .take(config.stopsToShow)
                .toList()
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

    private fun stopArrivalsUpdates() {
        arrivalRequestsTimer.purge()
    }

    private fun requestArrivals(stopPoint: TflStopPoint, section: Section, updateArrivalsTask: TimerTask) {
        api.getArrivals(stopPoint) {
            onArrivalsResponse(it, section, updateArrivalsTask)
        }
    }
}
