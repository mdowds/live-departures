package com.mdowds.livedepartures.departurespage

import com.mdowds.livedepartures.ArrivalsDataSource
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.helpers.TestDataFactory.makeConfig
import com.mdowds.livedepartures.helpers.TestDataFactory.makeTflArrivalPrediction
import com.mdowds.livedepartures.helpers.TestDataFactory.makeTflStopPoint
import com.mdowds.livedepartures.helpers.TestDataFactory.makeTflStopPoints
import com.mdowds.livedepartures.networking.model.TflArrivalPrediction
import com.mdowds.livedepartures.networking.model.TflStopPoints
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DeparturesPresenterTests {

    private val mockView = mock<DeparturesView>()
    private val mockDataSource = ArrivalsDataSource(mock(), makeConfig(), mock())

    private lateinit var presenter: DeparturesPresenter

    @Before
    fun setUp(){
        presenter = DeparturesPresenter(mockView, Mode.Bus, makeConfig(), mockDataSource, makeTflStopPoints().places)
    }

    //region filterStopPoints

    @Test
    fun `filterStopPoints returns a maximum of 5 stop points`() {
        val stopPoints = makeTflStopPoints(10).places
        val filtered = presenter.filterStopPoints(stopPoints)
        assertEquals(5, filtered.count())
    }

    @Test
    fun `filterStopPoints filters out points without the specified mode`() {
        val stopPoints = TflStopPoints(listOf(
                makeTflStopPoint(modes = listOf(Mode.Bus, Mode.Tube)),
                makeTflStopPoint(modes = listOf(Mode.Tube))
        )).places
        val filtered = presenter.filterStopPoints(stopPoints)

        assertTrue(filtered.all { it.modes.contains(Mode.Bus.tflName) })
    }

    @Test
    fun `filterStopPoints doesn't filter points based on mode if mode is null`() {
        val presenter = DeparturesPresenter(mockView, null, makeConfig(), mockDataSource, makeTflStopPoints().places)
        val stopPoints = TflStopPoints(listOf(
                makeTflStopPoint(modes = listOf(Mode.Bus, Mode.Tube)),
                makeTflStopPoint(modes = listOf(Mode.Tube))
        )).places
        val filtered = presenter.filterStopPoints(stopPoints)

        assertEquals(2, filtered.count())
    }

    @Test
    fun `filterStopPoints filters out points with no lines`() {
        val stopPoints = TflStopPoints(listOf(
                makeTflStopPoint(),
                makeTflStopPoint(lines = listOf())
        )).places
        val filtered = presenter.filterStopPoints(stopPoints)

        assertTrue(filtered.all { it.lines.isNotEmpty() })
    }

    //endregion

    //region onNewArrivalsResponse

    fun `onNewArrivalsResponse ignores arrivals for stop points it's not displaying`() {
        val response = Pair(makeTflStopPoint(naptanId = "DIFF"), listOf<TflArrivalPrediction>())
        presenter.onNewArrivalsResponse(response)

        verify(mockView, never()).updateResults(any(), any())
    }

    @Test
    fun `onNewArrivalsResponse updates the view with new items`() {
        val response = Pair(makeTflStopPoint(), listOf(makeTflArrivalPrediction()))
        presenter.onNewArrivalsResponse(response)
        verify(mockView).updateResults(argThat { count() == 1 }, any())
    }

    @Test
    fun `onNewArrivalsResponse orders the departures by time`() {
        val firstArrival = makeTflArrivalPrediction(120)
        val secondArrival = makeTflArrivalPrediction(180)
        presenter.onNewArrivalsResponse(Pair(makeTflStopPoint(), listOf(secondArrival, firstArrival)))
        verify(mockView).updateResults(argThat { first().departureTime == "2 mins" }, any())
    }

    @Test
    fun `onNewArrivalsResponse updates the view with a maximum of 5 departures`() {
        val arrivals = (1..10).map { makeTflArrivalPrediction() }
        presenter.onNewArrivalsResponse(Pair(makeTflStopPoint(), arrivals))
        verify(mockView).updateResults(argThat { count() == 5 }, any())
    }

    @Test
    fun `onNewArrivalsResponse filters out arrivals without the specified mode`() {
        val arrivals = listOf(
                makeTflArrivalPrediction(mode = Mode.Bus),
                makeTflArrivalPrediction(mode = Mode.Tube)
        )
        presenter.onNewArrivalsResponse(Pair(makeTflStopPoint(), arrivals))
        verify(mockView).updateResults(argThat { count() == 1 }, any())
    }

    @Test
    fun `onNewArrivalsResponse doesn't filter based on mode if node is null`() {
        val presenter = DeparturesPresenter(mockView, null, makeConfig(), mockDataSource, makeTflStopPoints().places)
        val arrivals = listOf(
                makeTflArrivalPrediction(mode = Mode.Bus),
                makeTflArrivalPrediction(mode = Mode.Tube)
        )
        presenter.onNewArrivalsResponse(Pair(makeTflStopPoint(), arrivals))

        verify(mockView).updateResults(argThat { count() == 2 }, any())
    }

    //endregion
}