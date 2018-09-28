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

    private val arrivalPrediction = TflArrivalPrediction("Piccadilly Line", "Finsbury Park", "FBY", "Cockfosters", "CKF", 120, Mode.Tube.tflName, "Northbound - Platform 1")

    @Test
    fun `Creates a departure from a TflArrivalPrediction`() {
        val departure = Departure(arrivalPrediction)

        assertEquals(arrivalPrediction.lineName, departure.line)
        assertEquals(arrivalPrediction.destinationName, departure.destination)
    }

    @Test
    fun `Strips "Rail Station" from destination name`() {
        val departure = Departure(arrivalPrediction.copy(destinationName = "Barking Rail Station"))

        assertEquals("Barking", departure.destination)
    }

    @Test
    fun `Strips "Underground Station" from destination name`() {
        val departure = Departure(arrivalPrediction.copy(destinationName = "Cockfosters Underground Station"))
        assertEquals("Cockfosters", departure.destination)
    }

    @Test
    fun `Strips "DLR Station" from destination name`() {
        val departure = Departure(arrivalPrediction.copy(destinationName = "Stratford DLR Station"))
        assertEquals("Stratford", departure.destination)
    }

    @Test
    fun `Strips "(London)" from destination name`() {
        val departure = Departure(arrivalPrediction.copy(destinationName = "Stratford (London)"))
        assertEquals("Stratford", departure.destination)
    }



    @Test
    fun `Converts arrival time to minutes`() {
        val departure = Departure(arrivalPrediction)
        assertEquals("2 mins", departure.departureTime)
    }

    @Test
    fun `Converts arrival time to minutes and rounds`() {
        val departure = Departure(arrivalPrediction.copy(timeToStation = 130))
        assertEquals("2 mins", departure.departureTime)
    }

    @Test
    fun `Sets arrival time as "Due" when arrival in less than 1 minute`() {
        val departure = Departure(arrivalPrediction.copy(timeToStation = 50))
        assertEquals("Due", departure.departureTime)
    }

    @Test
    fun `Sets direction as empty string when none present`() {
        val departure = Departure(arrivalPrediction.copy(platformName = "Platform 1"))
        assertEquals("", departure.direction)
    }

    @Test
    fun `Extracts direction from platformName when present`() {
        val departure = Departure(arrivalPrediction)
        assertEquals("Northbound", departure.direction)
    }

    @Test
    fun `Sets platform name`() {
        val departure = Departure(arrivalPrediction.copy(platformName = "Platform 1"))
        assertEquals("Platform 1", departure.platform)
    }

    @Test
    fun `Extracts platform from platformName when direction present`() {
        val departure = Departure(arrivalPrediction)
        assertEquals("Platform 1", departure.platform)
    }

    @Test
    fun `Adds "Platform" to platform name if not present`() {
        val departure = Departure(arrivalPrediction.copy(platformName = "4a"))
        assertEquals("Platform 4a", departure.platform)
    }

    @Test
    fun `Sets isTerminating to true if the destination is the same as the station`() {
        val departure = Departure(arrivalPrediction.copy(naptanId = arrivalPrediction.destinationNaptanId))
        assertTrue(departure.isTerminating)
    }
}