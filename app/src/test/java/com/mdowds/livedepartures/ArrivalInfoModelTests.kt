package com.mdowds.livedepartures

import org.junit.Test
import org.junit.Assert.*

class ArrivalInfoModelTests {

    private val arrivalPrediction = TflArrivalPrediction("Piccadilly Line", "Cockfosters", 120)

    @Test
    fun `Creates an ArrivalInfoModel from a TflArrivalPrediction`() {
        val arrivalInfoModel = ArrivalInfoModel(arrivalPrediction)

        assertEquals(arrivalPrediction.lineName, arrivalInfoModel.line)
        assertEquals(arrivalPrediction.destinationName, arrivalInfoModel.destination)
    }

    @Test
    fun `Strips "Rail Station" from destination name`() {
        val arrivalPrediction = TflArrivalPrediction("London Overground", "Barking Rail Station", 120)
        val arrivalInfoModel = ArrivalInfoModel(arrivalPrediction)

        assertEquals("Barking", arrivalInfoModel.destination)
    }

    @Test
    fun `Converts arrival time to minutes`() {
        val arrivalInfoModel = ArrivalInfoModel(arrivalPrediction)

        assertEquals("2 mins", arrivalInfoModel.arrivalTime)
    }

    @Test
    fun `Converts arrival time to minutes and rounds`() {
        val arrivalPrediction = TflArrivalPrediction("Piccadilly Line", "Cockfosters", 130)
        val arrivalInfoModel = ArrivalInfoModel(arrivalPrediction)

        assertEquals("2 mins", arrivalInfoModel.arrivalTime)
    }

    @Test
    fun `Sets arrival time as "Due" when arrival in less than 1 minute`() {
        val arrivalPrediction = TflArrivalPrediction("Piccadilly Line", "Cockfosters", 50)
        val arrivalInfoModel = ArrivalInfoModel(arrivalPrediction)

        assertEquals("Due", arrivalInfoModel.arrivalTime)
    }
}