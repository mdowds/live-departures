package com.mdowds.livedepartures.departurespage

import com.mdowds.livedepartures.*
import com.mdowds.livedepartures.networking.model.TflStopPoint
import com.mdowds.livedepartures.utils.AppConfig
import com.mdowds.livedepartures.utils.Config

class DeparturesPresenter(private val view: DeparturesView,
                          private val mode: Mode?,
                          private val config: Config,
                          private val arrivalsDataSource: ArrivalsDataSource,
                          allStopPoints: List<TflStopPoint>) {

    companion object {
        fun create(view: DeparturesFragment, arrivalsDataSource: ArrivalsDataSource, allStopPoints: List<TflStopPoint>): DeparturesPresenter {

            val config = AppConfig(view.resources).config

            return DeparturesPresenter(view,
                    view.mode,
                    config,
                    arrivalsDataSource,
                    allStopPoints
            )
        }
    }

    private val stopPoints = filterStopPoints(allStopPoints)

    init {
        stopPoints.forEach {
            arrivalsDataSource.addStopPoint(it)
            view.addStopSection(StopPoint(it))
        }
        arrivalsDataSource.addObserver(this::onNewArrivalsResponse)
    }

    fun onStop() {
        arrivalsDataSource.removeObserver(this::onNewArrivalsResponse)
    }

    fun onNewArrivalsResponse(response: ArrivalsResponse) {
        val (tflStopPoint, newResults) = response
        if (stopPoints.contains(tflStopPoint)) {
            val newResultsOrdered = newResults.sortedBy { it.timeToStation }
            val newDepartures = newResultsOrdered
                    .asSequence()
                    .filter { mode == null || it.modeName == mode.tflName }
                    .take(config.departuresPerStop)
                    .map { Departure(it) }
                    .toList()

            view.updateResults(newDepartures, StopPoint(tflStopPoint))
        }
    }

    fun filterStopPoints(allStopPoints: List<TflStopPoint>): List<TflStopPoint> =
            allStopPoints
                .asSequence()
                .filter { it.lines.isNotEmpty() }
                .filter { mode == null || it.modes.contains(mode.tflName) }
                .take(config.stopsToShow)
                .toList()
}
