package com.mdowds.livedepartures

import android.graphics.Color
import com.mdowds.livedepartures.networking.TflArrivalPrediction
import com.mdowds.livedepartures.networking.TflStopPoint

data class Departure(val line: String, val destination: String, val departureTime: String) {

    constructor(tflArrivalPrediction: TflArrivalPrediction) : this(
            tflArrivalPrediction.lineName,
            convertStationName(tflArrivalPrediction.destinationName),
            formatArrivalTime(tflArrivalPrediction.timeToStation)
    )
}

data class StopPoint(val name: String, val indicator: String?) {
    constructor(tflStopPoint: TflStopPoint) : this(
            convertStationName(tflStopPoint.commonName),
            tflStopPoint.indicator
    )
}

enum class Mode(val tflName: String, val color: Int) {
    Bus("bus", Color.rgb(220, 36, 31)),
    Tube("tube", Color.rgb(0, 25, 168)),
    Overground("overground", Color.rgb(239, 123, 16));
}

private fun convertStationName(name: String): String {
    return listOf(" Rail Station", " Underground Station").fold(name) { currentName, textToReplace ->
        currentName.replace(textToReplace, "")
    }
}

private fun formatArrivalTime(arrivalInSeconds: Int): String {
    val arrivalInMinutes = (arrivalInSeconds / 60)
    return if(arrivalInMinutes == 0) "Due" else "$arrivalInMinutes mins"
}
