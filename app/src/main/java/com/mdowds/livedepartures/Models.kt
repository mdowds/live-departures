package com.mdowds.livedepartures

import android.graphics.Color
import com.mdowds.livedepartures.networking.TflArrivalPrediction
import com.mdowds.livedepartures.networking.TflStopPoint

data class Departure(val line: String, val destination: String, val departureTime: String, val mode: Mode?, val direction: String, val platform: String) {

    constructor(tflArrivalPrediction: TflArrivalPrediction) : this(
            tflArrivalPrediction.lineName,
            convertStationName(tflArrivalPrediction.destinationName),
            formatArrivalTime(tflArrivalPrediction.timeToStation),
            Mode.fromModeName(tflArrivalPrediction.modeName),
            extractDirection(tflArrivalPrediction.platformName),
            extractPlatform(tflArrivalPrediction.platformName)
    )
}

data class StopPoint(val name: String, val indicator: String?) {
    constructor(tflStopPoint: TflStopPoint) : this(
            convertStationName(tflStopPoint.commonName),
            tflStopPoint.indicator
    )
}

enum class Mode(val tflName: String, val displayName: String, val color: Int, val canGetArrivals: Boolean = true) {
    Bus("bus", "Bus", Color.rgb(220, 36, 31)),
    Tube("tube", "Tube", Color.rgb(0, 25, 168)),
    DLR("dlr", "DLR", Color.rgb(0, 175, 173)),
    Overground("overground", "Overground", Color.rgb(239, 123, 16)),
    Tram("tram", "Tram", Color.rgb(0, 189, 25)),
    NationalRail("national-rail", "National Rail", Color.rgb(255, 255, 255), false),
    TflRail("tflrail", "TfL Rail", Color.rgb(0, 25, 128)),
    RiverBoat("river-boat", "River Boat", Color.rgb(0, 160, 226));

    companion object {
        fun fromModeName(name: String) = values().find { it.tflName == name }
    }
}

private fun convertStationName(name: String): String {
    return listOf(" Rail Station", " Underground Station", " DLR Station", " (London)")
            .fold(name) { currentName, textToReplace ->
                currentName.replace(textToReplace, "")
            }
}

private fun formatArrivalTime(arrivalInSeconds: Int): String {
    val arrivalInMinutes = (arrivalInSeconds / 60)
    return if (arrivalInMinutes == 0) "Due" else "$arrivalInMinutes mins"
}

private fun extractDirection(platformName: String): String {
    return when {
        platformName.contains(" -") -> platformName.substring(0, platformName.indexOf(" -"))
        else -> ""
    }
}
private fun extractPlatform(platformName: String): String {
    return when {
        platformName.contains(" -") -> platformName.substring(platformName.indexOf(" -") + 3)
        !platformName.contains("Platform") -> "Platform $platformName"
        else -> platformName
    }
}
