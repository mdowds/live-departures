package com.mdowds.livedepartures

import com.mdowds.livedepartures.helpers.TestDataFactory.makeConfig
import com.mdowds.livedepartures.helpers.TestDataFactory.makeTflStopPoint
import com.mdowds.livedepartures.networking.TransportInfoApi
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class ArrivalsDataSourceTests {

    private val mockApi = mock<TransportInfoApi>()
    private val mockTimer = mock<Timer>()

    private lateinit var dataSource: ArrivalsDataSource

    @Before
    fun setUp(){
        dataSource = ArrivalsDataSource(mockApi, makeConfig(), mockTimer)
    }

    //region addStopPoint

    @Test
    fun `addStopPoint adds stop point to the fetch list`(){
        val stopPoint = makeTflStopPoint()
        dataSource.addStopPoint(stopPoint)
        dataSource.requestArrivalsForAll()

        verify(mockApi, times(2)).getArrivals(eq(stopPoint), any())
    }

    @Test
    fun `addStopPoint makes an immediate request for the stop point`(){
        val stopPoint = makeTflStopPoint()
        dataSource.addStopPoint(stopPoint)

        verify(mockApi).getArrivals(eq(stopPoint), any())
    }

    @Test
    fun `addStopPoint does not add stop point to the fetch list if it is already present`(){
        dataSource.addStopPoint(makeTflStopPoint())
        dataSource.addStopPoint(makeTflStopPoint())
        dataSource.requestArrivalsForAll()

        verify(mockApi, times(3)).getArrivals(any(), any())
    }

    @Test
    fun `addStopPoint does not add stop point to the fetch list if one with the same ID is already present`(){
        dataSource.addStopPoint(makeTflStopPoint())
        dataSource.addStopPoint(makeTflStopPoint(name = "Different name"))
        dataSource.requestArrivalsForAll()

        verify(mockApi, times(3)).getArrivals(any(), any())
    }

    //endregion

    //region onArrivalsResponse

    @Test
    fun `Removes stop point from fetch list if no arrivals returned`(){
        dataSource.addStopPoint(makeTflStopPoint())
        dataSource.onArrivalsResponse(makeTflStopPoint(), listOf())
        dataSource.requestArrivalsForAll()

        verify(mockApi, times(1)).getArrivals(any(), any())

    }

    //endregion
}