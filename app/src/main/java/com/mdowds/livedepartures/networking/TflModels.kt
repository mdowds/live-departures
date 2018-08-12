package com.mdowds.livedepartures.networking

data class TflArrivalPrediction(val lineName: String, val destinationName: String, val timeToStation: Int, val modeName: String)

data class TflStopPoints(val places: List<TflStopPoint>)

data class TflStopPoint(val commonName: String, val naptanId: String, val indicator: String?, val lines: List<Any>, val modes: List<String>)
