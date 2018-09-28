package com.mdowds.livedepartures.departurespage

import android.view.View
import android.widget.TextView
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.helpers.TestDataFactory.makeDepartureModel
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import kotlinx.android.synthetic.main.departure_info.view.*
import kotlinx.android.synthetic.main.stop_name.view.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StopSectionTests {

    @Test
    fun `getContentItemsTotal returns the number of ArrivalModels`() {
        val section = StopSection("", "", listOf(makeDepartureModel(), makeDepartureModel()))
        assertEquals(section.contentItemsTotal, 2)
    }

    @Test
    fun `getHeaderViewHolder returns a StopNameViewHolder`() {
        val section = StopSection("", "", listOf())
        val holder = section.getHeaderViewHolder(mock())
        assertTrue(holder is StopSection.StopNameViewHolder)
    }

    //region onBindHeaderViewHolder()

    @Test
    fun `onBindHeaderViewHolder updates the header with the values of the model`() {
        val section = StopSection("Stop name", "Stop A", listOf())
        val mockViewContainer = MockStopNameViewContainer()
        val holder = StopSection.StopNameViewHolder(mockViewContainer.view)

        section.onBindHeaderViewHolder(holder)

        verify(mockViewContainer.stopName).text = "Stop name"
        verify(mockViewContainer.stopIndicator).text = "Stop A"
    }

    @Test
    fun `onBindHeaderViewHolder hides the no stop indicator text when there is no indicator`() {
        val section = StopSection("Stop name", null, listOf())
        val mockViewContainer = MockStopNameViewContainer()
        val holder = StopSection.StopNameViewHolder(mockViewContainer.view)

        section.onBindHeaderViewHolder(holder)

        verify(mockViewContainer.stopIndicator).visibility = View.GONE
    }

    @Test
    fun `onBindHeaderViewHolder shows the no departures text when departures is empty and section is loaded`() {
        val section = StopSection("Stop name", "", listOf())
        section.state = Section.State.LOADED
        val mockViewContainer = MockStopNameViewContainer()
        val holder = StopSection.StopNameViewHolder(mockViewContainer.view)

        section.onBindHeaderViewHolder(holder)

        verify(mockViewContainer.noDeparturesText).visibility = View.VISIBLE
    }

    @Test
    fun `onBindHeaderViewHolder hides the no departures text when departures is empty and section is loading`() {
        val section = StopSection("Stop name", "", listOf())
        section.state = Section.State.LOADING
        val mockViewContainer = MockStopNameViewContainer()
        val holder = StopSection.StopNameViewHolder(mockViewContainer.view)

        section.onBindHeaderViewHolder(holder)

        verify(mockViewContainer.noDeparturesText).visibility = View.GONE
    }

    //endregion

    //region getItemViewHolder

    @Test
    fun `getItemViewHolder returns an ArrivalInfoViewHolder`() {
        val section = StopSection("", "", listOf())
        val holder = section.getItemViewHolder(mock())
        assertTrue(holder is StopSection.DepartureInfoViewHolder)
    }

    //endregion

    //region onBindItemViewHolder

    @Test
    fun `onBindItemViewHolder updates the view with the correct values for a bus departure`() {
        val model = makeDepartureModel(line = "55", destination = "Oxford Circus", departureTime = "3 mins", mode = Mode.Bus)
        val section = StopSection("", "", listOf(model))
        val mockViewContainer = MockArrivalInfoViewContainer()
        val holder = StopSection.DepartureInfoViewHolder(mockViewContainer.view)

        section.onBindItemViewHolder(holder, 0)

        verify(mockViewContainer.title).text = "55"
        verify(mockViewContainer.subtitle).text = "Oxford Circus"
        verify(mockViewContainer.arrivalTime).text = "3 mins"
    }

    @Test
    fun `onBindItemViewHolder updates the view with the correct values for a tube departure`() {
        val model = makeDepartureModel(line = "Victoria", destination = "Brixton", departureTime = "3 mins", mode = Mode.Tube, direction = "Southbound")
        val section = StopSection("", "", listOf(model))
        val mockViewContainer = MockArrivalInfoViewContainer()
        val holder = StopSection.DepartureInfoViewHolder(mockViewContainer.view)

        section.onBindItemViewHolder(holder, 0)

        verify(mockViewContainer.title).text = "Victoria - Southbound"
        verify(mockViewContainer.subtitle).text = "Brixton"
        verify(mockViewContainer.arrivalTime).text = "3 mins"
    }

    @Test
    fun `onBindItemViewHolder updates the view with the correct values for an overground departure`() {
        val model = makeDepartureModel(line = "London Overground", destination = "Stratford", departureTime = "3 mins", mode = Mode.Overground, platform = "Platform 2")
        val section = StopSection("", "", listOf(model))
        val mockViewContainer = MockArrivalInfoViewContainer()
        val holder = StopSection.DepartureInfoViewHolder(mockViewContainer.view)

        section.onBindItemViewHolder(holder, 0)

        verify(mockViewContainer.title).text = "Platform 2"
        verify(mockViewContainer.subtitle).text = "Stratford"
        verify(mockViewContainer.arrivalTime).text = "3 mins"
    }

    @Test
    fun `onBindItemViewHolder sets the title to 'arriving' when the service is terminating`() {
        val model = makeDepartureModel(isTerminating = true)
        val section = StopSection("", "", listOf(model))
        val mockViewContainer = MockArrivalInfoViewContainer()
        val holder = StopSection.DepartureInfoViewHolder(mockViewContainer.view)

        section.onBindItemViewHolder(holder, 0)

        verify(mockViewContainer.subtitle).text = "Arriving"
    }

    //endregion
    
    class MockArrivalInfoViewContainer {
        val title = mock<TextView>()
        val subtitle = mock<TextView>()
        val arrivalTime = mock<TextView>()

        val view = mock<View>{
            on { title }.doReturn(title)
            on { subtitle }.doReturn(subtitle)
            on { arrival_time }.doReturn(arrivalTime)
        }
    }

    class MockStopNameViewContainer {
        val stopName = mock<TextView>()
        val stopIndicator = mock<TextView>()
        val noDeparturesText = mock<TextView>()

        val view = mock<View>{
            on { stop_name }.doReturn(stopName)
            on { stop_indicator }.doReturn(stopIndicator)
            on { no_departures_text }.doReturn(noDeparturesText)
        }
    }
}