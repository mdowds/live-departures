package com.mdowds.livedepartures.departurespage

import com.mdowds.livedepartures.Departure
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.networking.TflArrivalPrediction
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DepartureTests {

    private val arrivalPrediction = TflArrivalPrediction("Piccadilly Line", "Cockfosters", 120, Mode.Tube.tflName)

    @Test
    fun `Creates an ArrivalInfoModel from a TflArrivalPrediction`() {
        val arrivalInfoModel = Departure(arrivalPrediction)

        assertEquals(arrivalPrediction.lineName, arrivalInfoModel.line)
        assertEquals(arrivalPrediction.destinationName, arrivalInfoModel.destination)
    }

    @Test
    fun `Strips "Rail Station" from destination name`() {
        val arrivalPrediction = TflArrivalPrediction("London Overground", "Barking Rail Station", 120, Mode.Overground.tflName)
        val arrivalInfoModel = Departure(arrivalPrediction)

        assertEquals("Barking", arrivalInfoModel.destination)
    }

    @Test
    fun `Strips "Underground Station" from destination name`() {
        val arrivalPrediction = TflArrivalPrediction("Piccadilly Line", "Cockfosters Underground Station", 130, Mode.Tube.tflName)
        val arrivalInfoModel = Departure(arrivalPrediction)

        assertEquals("Cockfosters", arrivalInfoModel.destination)
    }

    @Test
    fun `Converts arrival time to minutes`() {
        val arrivalInfoModel = Departure(arrivalPrediction)

        assertEquals("2 mins", arrivalInfoModel.departureTime)
    }

    @Test
    fun `Converts arrival time to minutes and rounds`() {
        val arrivalPrediction = TflArrivalPrediction("Piccadilly Line", "Cockfosters", 130, Mode.Tube.tflName)
        val arrivalInfoModel = Departure(arrivalPrediction)

        assertEquals("2 mins", arrivalInfoModel.departureTime)
    }

    @Test
    fun `Sets arrival time as "Due" when arrival in less than 1 minute`() {
        val arrivalPrediction = TflArrivalPrediction("Piccadilly Line", "Cockfosters", 50, Mode.Tube.tflName)
        val arrivalInfoModel = Departure(arrivalPrediction)

        assertEquals("Due", arrivalInfoModel.departureTime)
    }
}