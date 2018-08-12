package com.mdowds.livedepartures

import com.mdowds.livedepartures.helpers.TestDataFactory
import com.mdowds.livedepartures.helpers.TestDataFactory.makeConfig
import com.mdowds.livedepartures.networking.TransportInfoApi
import com.mdowds.livedepartures.utils.LocationManager
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test

class NearbyStopPointsDataSourceTests {

    private val mockLocationManager = mock<LocationManager>()
    private val mockApi = mock<TransportInfoApi>()

    private lateinit var dataSource: NearbyStopPointsDataSource

    @Before
    fun setUp(){
        dataSource = NearbyStopPointsDataSource(makeConfig(), mockLocationManager, mockApi)
    }

    //region onLocationResponse

    @Test
    fun `onLocationResponse requests nearby stops for first location response`() {
        dataSource.onLocationResponse(TestDataFactory.makeLocation(1.0, -1.0))
        verify(mockApi).getNearbyStops(eq(1.0), eq(-1.0), any())
    }

    @Test
    fun `onLocationResponse doesn't request nearby stops if location has changed by less than 10 metres`() {
        val originalLocation = TestDataFactory.makeLocation(1.0, -1.0)
        whenever(originalLocation.distanceTo(any())).thenReturn(2.0f)

        dataSource.onLocationResponse(originalLocation)
        dataSource.onLocationResponse(TestDataFactory.makeLocation(1.0, -1.0))
        verify(mockApi, times(1)).getNearbyStops(any(), any(), any())
    }

    @Test
    fun `onLocationResponse does request nearby stops if location has changed by more than 10 metres`() {
        val originalLocation = TestDataFactory.makeLocation(1.0, -1.0)
        whenever(originalLocation.distanceTo(any())).thenReturn(11.0f)

        dataSource.onLocationResponse(originalLocation)
        dataSource.onLocationResponse(TestDataFactory.makeLocation(1.1, -1.0))
        verify(mockApi, times(2)).getNearbyStops(any(), any(), any())
    }

    //endregion

}