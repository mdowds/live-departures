package com.mdowds.livedepartures.helpers

import android.location.Location
import com.google.android.gms.location.LocationResult
import com.mdowds.livedepartures.Arrival
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

    fun makeTflStopPoint(): TflStopPoint = TflStopPoint("Name", "STOP", "Indicator", listOf("Line"))

    fun makeTflArrivalPrediction(time: Int = 1) : TflArrivalPrediction = TflArrivalPrediction("Line", "Destination", time)

    fun makeArrivalModel() : Arrival = Arrival("Line", "Destination", "Arrival Time")

    fun makeConfig() : Config = Config(5,5,10,10,false,"")
}