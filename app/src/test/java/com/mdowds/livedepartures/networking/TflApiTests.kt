package com.mdowds.livedepartures.networking

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class TflApiTests {

    private val requestQueue = mock<RequestQueue>()
    private val tflApi = TflApi(requestQueue)

    @Test
    fun `getNearbyStops makes a call to the correct endpoint`() {
        tflApi.getNearbyStops(0.1, 1.5) {}
        verify(requestQueue).add(argThat<Request<String>> { url == "https://api.tfl.gov.uk/Place?type=NaptanMetroStation,NaptanRailStation,NaptanPublicBusCoachTram,NaptanFerryPort&lat=0.1&lon=1.5&radius=200"})
    }

    @Test
    fun `getArrivals makes a call to the correct endpoint`() {
        tflApi.getArrivals(TflStopPoint("Waterloo", "WLOO", "", listOf(), listOf())) {}
        verify(requestQueue).add(argThat<Request<String>> { url == "https://api.tfl.gov.uk/StopPoint/WLOO/Arrivals"})
    }

}
