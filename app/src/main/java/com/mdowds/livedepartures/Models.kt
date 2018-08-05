package com.mdowds.livedepartures

import com.mdowds.livedepartures.networking.TflArrivalPrediction
import com.mdowds.livedepartures.networking.TflStopPoint

data class Departure(val line: String, val destination: String, val departureTime: String) {

    constructor(tflArrivalPrediction: TflArrivalPrediction) : this(
            tflArrivalPrediction.lineName,
            stripRailStationFromName(tflArrivalPrediction.destinationName),
            formatArrivalTime(tflArrivalPrediction.timeToStation)
    )
}

data class StopPoint(val name: String, val indicator: String?) {
    constructor(tflStopPoint: TflStopPoint) : this(
            stripRailStationFromName(tflStopPoint.commonName),
            tflStopPoint.indicator
    )
}

private fun stripRailStationFromName(name: String): String = name.replace(" Rail Station", "")

private fun formatArrivalTime(arrivalInSeconds: Int): String {
    val arrivalInMinutes = (arrivalInSeconds / 60)
    return if(arrivalInMinutes == 0) "Due" else "$arrivalInMinutes mins"
}
