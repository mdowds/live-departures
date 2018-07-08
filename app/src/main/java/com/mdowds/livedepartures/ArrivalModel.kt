package com.mdowds.livedepartures

data class ArrivalModel(val line: String, val destination: String, val arrivalTime: String) {

    constructor(tflArrivalPrediction: TflArrivalPrediction) : this(
            tflArrivalPrediction.lineName,
            stripRailStationFromName(tflArrivalPrediction.destinationName),
            formatArrivalTime(tflArrivalPrediction.timeToStation)
    )
}

private fun stripRailStationFromName(name: String): String = name.replace(" Rail Station", "")

private fun formatArrivalTime(arrivalInSeconds: Int): String {
    val arrivalInMinutes = (arrivalInSeconds / 60)
    return if(arrivalInMinutes == 0) "Due" else "$arrivalInMinutes mins"
}
