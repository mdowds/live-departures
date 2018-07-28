package com.mdowds.livedepartures

import android.view.View
import android.widget.TextView
import com.mdowds.livedepartures.helpers.TestDataFactory.makeArrivalModel
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import kotlinx.android.synthetic.main.arrival_info.view.*
import kotlinx.android.synthetic.main.stop_name.view.*
import org.junit.Assert.*
import org.junit.Test

class StopSectionTests {

    @Test
    fun `getContentItemsTotal returns the number of ArrivalModels`() {
        val section = StopSection("", "", listOf(makeArrivalModel(), makeArrivalModel()))
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
    fun `onBindHeaderViewHolder shows the no departures text when arrivals is empty and section is loaded`() {
        val section = StopSection("Stop name", "", listOf())
        section.state = Section.State.LOADED
        val mockViewContainer = MockStopNameViewContainer()
        val holder = StopSection.StopNameViewHolder(mockViewContainer.view)

        section.onBindHeaderViewHolder(holder)

        verify(mockViewContainer.noDeparturesText).visibility = View.VISIBLE
    }

    @Test
    fun `onBindHeaderViewHolder hides the no departures text when arrivals is empty and section is loading`() {
        val section = StopSection("Stop name", "", listOf())
        section.state = Section.State.LOADING
        val mockViewContainer = MockStopNameViewContainer()
        val holder = StopSection.StopNameViewHolder(mockViewContainer.view)

        section.onBindHeaderViewHolder(holder)

        verify(mockViewContainer.noDeparturesText).visibility = View.GONE
    }

    //endregion

    @Test
    fun `getItemViewHolder returns an ArrivalInfoViewHolder`() {
        val section = StopSection("", "", listOf())
        val holder = section.getItemViewHolder(mock())
        assertTrue(holder is StopSection.ArrivalInfoViewHolder)
    }

    @Test
    fun `onBindItemViewHolder updates the view with the values of the model`() {
        val model = makeArrivalModel()
        val section = StopSection("", "", listOf(model))
        val mockViewContainer = MockArrivalInfoViewContainer()
        val holder = StopSection.ArrivalInfoViewHolder(mockViewContainer.view)

        section.onBindItemViewHolder(holder, 0)

        verify(mockViewContainer.routeName).text = "Line"
        verify(mockViewContainer.routeDestination).text = "Destination"
        verify(mockViewContainer.arrivalTime).text = "Arrival Time"
    }
    
    class MockArrivalInfoViewContainer {
        val routeName = mock<TextView>()
        val routeDestination = mock<TextView>()
        val arrivalTime = mock<TextView>()

        val view = mock<View>{
            on { route_name }.doReturn(routeName)
            on { route_destination }.doReturn(routeDestination)
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