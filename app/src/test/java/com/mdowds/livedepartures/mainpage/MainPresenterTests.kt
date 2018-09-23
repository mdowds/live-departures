package com.mdowds.livedepartures.mainpage

import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.NearbyStopPointsDataSource
import com.mdowds.livedepartures.helpers.TestDataFactory
import com.mdowds.livedepartures.networking.TflStopPoint
import com.mdowds.livedepartures.networking.TflStopPoints
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainPresenterTests {

    private val mockView = mock<MainView>()
    private val mockDataSource = NearbyStopPointsDataSource(TestDataFactory.makeConfig(), mock(), mock())

    private lateinit var presenter: MainPresenter

    @Before
    fun setUp(){
        presenter = MainPresenter(mockView, mockDataSource)
    }

    //region stopPointsUpdated

    @Test
    fun `stopPointsUpdated passes the modes of the stop points to the view`() {
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus)),
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Tube))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))
        verify(mockView).updateModes(listOf(Mode.Bus, Mode.Tube))
    }

    @Test
    fun `stopPointsUpdated removes duplicate modes`() {
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus)),
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus, Mode.Tube))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))
        verify(mockView).updateModes(listOf(Mode.Bus, Mode.Tube))
    }

    @Test
    fun `stopPointsUpdated ignores unrecognised modes`() {
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus)),
                TflStopPoint("Name", "STOP", "Indicator", listOf("Line"), listOf("helicopter"))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))
        verify(mockView).updateModes(listOf(Mode.Bus))
    }

    @Test
    fun `stopPointsUpdated orders modes by order they are defined in the enum`() {
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Tube, Mode.RiverBoat, Mode.Bus))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))
        verify(mockView).updateModes(listOf(Mode.Bus, Mode.Tube, Mode.RiverBoat))
    }

    //endregion stopPointsUpdated
}