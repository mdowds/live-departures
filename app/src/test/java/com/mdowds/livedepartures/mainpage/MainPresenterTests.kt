package com.mdowds.livedepartures.mainpage

import android.graphics.Color
import com.mdowds.livedepartures.ArrivalsDataSource
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.NearbyStopPointsDataSource
import com.mdowds.livedepartures.helpers.TestDataFactory
import com.mdowds.livedepartures.networking.model.TflStopPoint
import com.mdowds.livedepartures.networking.model.TflStopPoints
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainPresenterTests {

    private val mockView = mock<MainView>()

    private lateinit var presenter: MainPresenter

    @Before
    fun setUp(){
        val mockStopPointsDataSource = NearbyStopPointsDataSource(TestDataFactory.makeConfig(), mock(), mock())
        val mockArrivalsDataSource = ArrivalsDataSource(mock(), TestDataFactory.makeConfig(), mock())
        presenter = MainPresenter(mockView, mockStopPointsDataSource, mockArrivalsDataSource)
    }

    //region stopPointsUpdated

    @Test
    fun `stopPointsUpdated extracts the modes of the stop points`() {
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus)),
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Tube))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))
        assertEquals(listOf(Mode.Bus, Mode.Tube), presenter.modes)
    }

    @Test
    fun `stopPointsUpdated removes duplicate modes`() {
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus)),
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus, Mode.Tube))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))
        assertEquals(listOf(Mode.Bus, Mode.Tube), presenter.modes)
    }

    @Test
    fun `stopPointsUpdated ignores unrecognised modes`() {
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus)),
                TflStopPoint("Name", "STOP", "Indicator", listOf("Line"), listOf("helicopter"))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))
        assertEquals(listOf(Mode.Bus), presenter.modes)
    }

    @Test
    fun `stopPointsUpdated orders modes by order they are defined in the enum`() {
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Tube, Mode.RiverBus, Mode.Bus))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))
        assertEquals(listOf(Mode.Bus, Mode.Tube, Mode.RiverBus), presenter.modes)
    }

    //endregion stopPointsUpdated

    //region onPageSelected

    @Test
    fun `onPageSelected sets the header to the right colours for the selected mode`(){
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus)),
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Tube))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))

        presenter.onPageSelected(1)
        verify(mockView).setHeaderBackgroundColor(Mode.Tube.color)
        verify(mockView).setHeaderTextColor(Color.WHITE)
    }

    @Test
    fun `onPageSelected changes the text colour to black when the mode colour is white`(){
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus)),
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.NationalRail))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))

        presenter.onPageSelected(1)
        verify(mockView).setHeaderBackgroundColor(Mode.NationalRail.color)
        verify(mockView).setHeaderTextColor(Color.BLACK)
    }

    //endregion

    //region onEnterAmbient

    @Test
    fun `onEnterAmbient sets the header colours to black and white`(){
        presenter.onEnterAmbient(null)
        verify(mockView).setHeaderBackgroundColor(Color.BLACK)
        verify(mockView).setHeaderTextColor(Color.WHITE)
    }

    //endregion

    // region onExitAmbient

    @Test
    fun `onExitAmbient sets the header colours back to full colour`(){
        val stopPoints = listOf(
                TestDataFactory.makeTflStopPoint(modes = listOf(Mode.Bus))
        )
        presenter.stopPointsUpdated(TflStopPoints(stopPoints))

        presenter.onExitAmbient()
        verify(mockView).setHeaderBackgroundColor(Mode.Bus.color)
        verify(mockView).setHeaderTextColor(Color.WHITE)
    }

    //endregion
}