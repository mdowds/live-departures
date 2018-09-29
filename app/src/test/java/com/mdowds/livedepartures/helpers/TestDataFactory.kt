package com.mdowds.livedepartures.helpers

import android.location.Location
import com.google.android.gms.location.LocationResult
import com.mdowds.livedepartures.Departure
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.networking.TflArrivalPrediction
import com.mdowds.livedepartures.networking.TflStopPoint
import com.mdowds.livedepartures.networking.TflStopPoints
import com.mdowds.livedepartures.utils.Config
import com.nhaarman.mockitokotlin2.mock

object TestDataFactory {

    fun makeLocationResult(): LocationResult = LocationResult.create(listOf(makeLocation()))

    fun makeLocation(lat: Double = 0.0, long: Double = 0.0): Location = mock {
        on { latitude }.thenReturn(lat)
        on { longitude }.thenReturn(long)
    }

    fun makeTflStopPoints(numberOfStopPoints: Int = 1): TflStopPoints {
        val stopPoints = (1..numberOfStopPoints).map { makeTflStopPoint() }
        return TflStopPoints(stopPoints)
    }

    fun makeTflStopPoint(name: String = "Name",
                         naptanId: String = "STOP",
                         lines: List<String> = listOf("Line"),
                         modes: List<Mode> = listOf(Mode.Bus)): TflStopPoint =
            TflStopPoint("Name",
                    naptanId,
                    "Indicator",
                    lines,
                    modes.map { it.tflName }
            )

    fun makeTflArrivalPrediction(time: Int = 1, mode: Mode = Mode.Bus) : TflArrivalPrediction = TflArrivalPrediction("Line", "Station", "STN", "Destination", "DST", time, mode.tflName, "Platform")

    fun makeDepartureModel(line: String = "Line",
                           destination: String = "Destination",
                           departureTime: String = "Due",
                           mode: Mode = Mode.Bus,
                           direction: String = "",
                           platform: String = "Platform",
                           isTerminating: Boolean = false): Departure =
            Departure(line, destination, departureTime, mode, direction, platform, isTerminating)

    fun makeConfig() : Config = Config(5,5,10,10, 200)
}