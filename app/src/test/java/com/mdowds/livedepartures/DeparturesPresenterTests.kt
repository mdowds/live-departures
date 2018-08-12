package com.mdowds.livedepartures

import com.mdowds.livedepartures.helpers.TestDataFactory.makeConfig
import com.mdowds.livedepartures.helpers.TestDataFactory.makeTflArrivalPrediction
import com.mdowds.livedepartures.helpers.TestDataFactory.makeTflStopPoints
import com.mdowds.livedepartures.networking.TflArrivalPrediction
import com.mdowds.livedepartures.networking.TransportInfoApi
import com.mdowds.livedepartures.utils.LocationManager
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import java.util.*

class DeparturesPresenterTests {

    private val mockView = mock<DeparturesView>()
    private val mockLocationManager = mock<LocationManager>()
    private val mockApi = mock<TransportInfoApi>()
    private val mockTimer = mock<Timer>()
    private val mockDataSource = NearbyStopPointsDataSource(makeConfig(), mock(), mock())

    private lateinit var presenter: DeparturesPresenter

    @Before
    fun setUp(){
        presenter = DeparturesPresenter(mockView, makeConfig(), mockApi, mockTimer, mockDataSource)
    }

    //region onResume

    @Test
    fun `onResume shows loading spinner if there are no current stop points`() {
        presenter.onResume()
        verify(mockView).showLoadingSpinner()
    }

    @Test
    fun `onResume does not show loading spinner if there are current stop points`() {
        mockDataSource.onStopPointsResponse(makeTflStopPoints())
        presenter.onResume()
        verify(mockView, never()).showLoadingSpinner()
    }

    // endregion

    //region onPause

    @Test
    fun `onPause stops arrivals requests`() {
        presenter.onPause()
        verify(mockTimer).purge()
    }

    //endregion

    //region update

    @Test
    fun `update creates a section on the view for each stop point`() {
        val stopPoints = makeTflStopPoints()
        presenter.update(stopPoints)
        verify(mockView, times(stopPoints.places.count())).addStopSection(any())
    }

    @Test
    fun `update creates a maximum of 5 sections on the view`() {
        val stopPoints = makeTflStopPoints(10)
        presenter.update(stopPoints)
        verify(mockView, times(5)).addStopSection(any())
    }

    @Test
    fun `update triggers a TimerTask for each stop point every 10 seconds`() {
        val stopPoints = makeTflStopPoints()
        presenter.update(stopPoints)
        verify(mockTimer, times(stopPoints.places.count())).scheduleAtFixedRate(any(), eq(0L), eq(10000L))
    }

    @Test
    fun `update passes a TimerTask that makes an arrivals request`() {
        val stopPoints = makeTflStopPoints()
        presenter.update(stopPoints)

        argumentCaptor<TimerTask>().apply {
            verify(mockTimer).scheduleAtFixedRate(capture(), any<Long>(), any())

            firstValue.run()
            verify(mockApi).getArrivals(any(), any())
        }
    }

    @Test
    fun `update triggers a maximum of 5 TimerTasks`() {
        val stopPoints = makeTflStopPoints(10)
        presenter.update(stopPoints)
        verify(mockTimer, times(5)).scheduleAtFixedRate(any(), any<Long>(), any())
    }

    @Test
    fun `update clears the current sections in the view`() {
        presenter.update(makeTflStopPoints())
        verify(mockView).removeStopSections()
    }

    @Test
    fun `update cancels all existing TimerTasks for arrivals requests`() {
        presenter.update(makeTflStopPoints())
        verify(mockTimer).purge()
    }

    //endregion

    //region onArrivalsResponse

    @Test
    fun `onArrivalsResponse response updates the view with new items`() {
        val arrival = makeTflArrivalPrediction()
        presenter.onArrivalsResponse(listOf(arrival), mock(), mock())
        verify(mockView).updateResults(argThat { count() == 1 }, any())
    }

    @Test
    fun `onArrivalsResponse orders the departures by time`() {
        val firstArrival = makeTflArrivalPrediction(120)
        val secondArrival = makeTflArrivalPrediction(180)
        presenter.onArrivalsResponse(listOf(secondArrival, firstArrival), mock(), mock())
        verify(mockView).updateResults(argThat { first().departureTime == "2 mins" }, any())
    }

    @Test
    fun `onArrivalsResponse updates the view with a maximum of 5 departures`() {
        val arrivals = (1..10).map { makeTflArrivalPrediction() }
        presenter.onArrivalsResponse(arrivals, mock(), mock())
        verify(mockView).updateResults(argThat { count() == 5 }, any())
    }

    @Test
    fun `onArrivalsResponse cancels the update arrivals task when the response contains no arrivals`() {
        val arrivals = listOf<TflArrivalPrediction>()
        val mockTask = mock<TimerTask>()
        presenter.onArrivalsResponse(arrivals, mock(), mockTask)
        verify(mockTask).cancel()
    }

    //endregion
}