package com.mdowds.livedepartures

data class TflArrivalPrediction(val lineName: String, val destinationName: String, val timeToStation: Int)

data class TflStopPoints(val places: List<TflStopPoint>)

data class TflStopPoint(val commonName: String, val naptanId: String)
