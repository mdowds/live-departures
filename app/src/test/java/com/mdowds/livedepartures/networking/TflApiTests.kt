package com.mdowds.livedepartures.networking

import android.os.Handler
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.mdowds.livedepartures.networking.model.TflArrivalPrediction
import com.mdowds.livedepartures.networking.model.TflStopPoint
import com.mdowds.livedepartures.networking.model.TflStopPoints
import com.nhaarman.mockitokotlin2.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class TflApiTests {

    private val requestQueue = mock<RequestQueue>()
    private val tflApi = TflApi(requestQueue)

    @Test
    fun `getNearbyStops makes a call to the correct endpoint`() {
        tflApi.getNearbyStops(0.1, 1.5, 200, {})
        verify(requestQueue).add(argThat<Request<String>> {
            url.startsWith("https://api.tfl.gov.uk/Place?type=NaptanMetroStation,NaptanRailStation,NaptanPublicBusCoachTram,NaptanFerryPort&lat=0.1&lon=1.5&radius=200")
        })
    }

    @Test
    fun `getNearbyStops calls the callback with a parsed response`(){
        var callbackCalledWith: TflStopPoints? = null
        tflApi.getNearbyStops(0.1, 1.5, 200, {callbackCalledWith = it})

        val stubResponse = """{ "places": [
            {"commonName": "Station", "naptanId": "1234", "lines": [], "modes": []}
        ]}""".trimMargin()

        argumentCaptor<StringRequest>().apply {
            verify(requestQueue).add(capture())
            sendResponse(firstValue, stubResponse)

            val firstStopPoint = callbackCalledWith!!.places[0]
            assertEquals("Station", firstStopPoint.commonName)
            assertEquals("1234", firstStopPoint.naptanId)
        }
    }

    @Test
    fun `getNearbyStops calls the error callback when the request results in an error`(){
        var errorCallbackCalled = false
        tflApi.getNearbyStops(0.1, 1.5, 200, {}, {errorCallbackCalled = true})

        argumentCaptor<StringRequest>().apply {
            verify(requestQueue).add(capture())
            firstValue.deliverError(VolleyError())
            assertTrue(errorCallbackCalled)
        }
    }

    @Test
    fun `getNearbyStops calls the error callback when the response is invalid`(){
        var errorCallbackCalled = false
        tflApi.getNearbyStops(0.1, 1.5, 200, {}, {errorCallbackCalled = true})

        argumentCaptor<StringRequest>().apply {
            verify(requestQueue).add(capture())
            sendResponse(firstValue, "")
            assertTrue(errorCallbackCalled)
        }
    }

    @Test
    fun `getArrivals makes a call to the correct endpoint`() {
        tflApi.getArrivals(TflStopPoint("Waterloo", "WLOO", "", listOf(), listOf()), {})
        verify(requestQueue).add(argThat<Request<String>> {
            url.startsWith("https://api.tfl.gov.uk/StopPoint/WLOO/Arrivals")
        })
    }

    @Test
    fun `getArrivals calls the callback with a parsed response`(){
        var callbackCalledWith: List<TflArrivalPrediction> = emptyList()
        tflApi.getArrivals(TflStopPoint("Waterloo", "WLOO", "", listOf(), listOf()), {callbackCalledWith = it})

        val stubResponse = """[{
            "lineName": "Line",
            "stationName": "Station",
            "naptanId": "1234",
            "destinationName": "Destination",
            "destinationNaptanId": "4321",
            "timeToStation": 100,
            "modeName": "bus",
            "platformName": "Platform"
        }]""".trimMargin()

        argumentCaptor<StringRequest>().apply {
            verify(requestQueue).add(capture())
            sendResponse(firstValue, stubResponse)

            val firstArrival = callbackCalledWith[0]
            assertEquals("Line", firstArrival.lineName)
            assertEquals("Station", firstArrival.stationName)
            assertEquals("1234", firstArrival.naptanId)
            assertEquals("Destination", firstArrival.destinationName)
            assertEquals("4321", firstArrival.destinationNaptanId)
            assertEquals(100, firstArrival.timeToStation)
            assertEquals("bus", firstArrival.modeName)
            assertEquals("Platform", firstArrival.platformName)
        }
    }

    @Test
    fun `getArrivals calls the error callback when the request results in an error`(){
        var errorCallbackCalled = false
        tflApi.getArrivals(TflStopPoint("Waterloo", "WLOO", "", listOf(), listOf()), {}, {errorCallbackCalled = true})

        argumentCaptor<StringRequest>().apply {
            verify(requestQueue).add(capture())
            firstValue.deliverError(VolleyError())
            assertTrue(errorCallbackCalled)
        }
    }

    @Test
    fun `getArrivals calls the error callback when the response is invalid`(){
        var errorCallbackCalled = false
        tflApi.getArrivals(TflStopPoint("Waterloo", "WLOO", "", listOf(), listOf()), {}, {errorCallbackCalled = true})

        argumentCaptor<StringRequest>().apply {
            verify(requestQueue).add(capture())
            sendResponse(firstValue, "")
            assertTrue(errorCallbackCalled)
        }
    }

    private fun sendResponse(request: StringRequest, response: String){
        ExecutorDelivery(Handler()).postResponse(request, Response.success(response, null))
    }
}
