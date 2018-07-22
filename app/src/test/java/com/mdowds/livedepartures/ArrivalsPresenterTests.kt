package com.mdowds.livedepartures

import com.mdowds.livedepartures.helpers.TestDataFactory.makeLocation
import com.mdowds.livedepartures.helpers.TestDataFactory.makeTflArrivalPrediction
import com.mdowds.livedepartures.helpers.TestDataFactory.makeTflStopPoints
import com.mdowds.livedepartures.networking.TransportInfoApi
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test

class ArrivalsPresenterTests {

    private val mockView = mock<ArrivalsView>()
    private val mockLocationManager = mock<LocationManager>()
    private val mockApi = mock<TransportInfoApi>()

    private lateinit var presenter: ArrivalsPresenter

    @Before
    fun setUp(){
        presenter = ArrivalsPresenter(mockView, mockLocationManager, mockApi)
    }

    @Test
    fun `onResume starts location updates`() {
        presenter.onResume()
        verify(mockLocationManager).startLocationUpdates(any())
    }

    @Test
    fun `onResume requests nearby stops upon location response`() {
        presenter.onResume()
        argumentCaptor<LastLocationCallback>().apply {
            verify(mockLocationManager).startLocationUpdates(capture())

            firstValue(makeLocation(1.0, -1.0))
            verify(mockApi).getNearbyStops(eq(1.0), eq(-1.0), any())
        }
    }

    @Test
    fun `onStopPointsResponse creates a section on the view for each stop point`() {
        val stopPoints = makeTflStopPoints()
        presenter.onStopPointsResponse(stopPoints)
        verify(mockView, times(stopPoints.places.count())).addStopSection(any())
    }

    @Test
    fun `onStopPointsResponse creates a maximum of 5 sections on the view`() {
        val stopPoints = makeTflStopPoints(10)
        presenter.onStopPointsResponse(stopPoints)
        verify(mockView, times(5)).addStopSection(any())
    }

    @Test
    fun `onStopPointsResponse makes an arrivals request for each stop point`() {
        val stopPoints = makeTflStopPoints()
        presenter.onStopPointsResponse(stopPoints)
        verify(mockApi, times(stopPoints.places.count())).getArrivals(any(), any())
    }

    @Test
    fun `onStopPointsResponse makes a maximum of 5 arrivals requests`() {
        val stopPoints = makeTflStopPoints(10)
        presenter.onStopPointsResponse(stopPoints)
        verify(mockApi, times(5)).getArrivals(any(), any())
    }

    @Test
    fun `onArrivalsResponse response updates the view with new items`() {
        val arrival = makeTflArrivalPrediction()
        presenter.onArrivalsResponse(listOf(arrival), mock())
        verify(mockView).updateResults(argThat { count() == 1 }, any())
    }

    @Test
    fun `onArrivalsResponse orders the arrivals by time`() {
        val firstArrival = makeTflArrivalPrediction(120)
        val secondArrival = makeTflArrivalPrediction(180)
        presenter.onArrivalsResponse(listOf(secondArrival, firstArrival), mock())
        verify(mockView).updateResults(argThat { first().arrivalTime == "2 mins" }, any())
    }

    @Test
    fun `onArrivalsResponse updates the view with a maximum of 5 arrivals`() {
        val arrivals = (0..10).map { makeTflArrivalPrediction() }
        presenter.onArrivalsResponse(arrivals, mock())
        verify(mockView).updateResults(argThat { count() == 5 }, any())
    }
}