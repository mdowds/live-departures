package com.mdowds.livedepartures.networking

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val BASE_URL = "https://api.tfl.gov.uk"

typealias ArrivalsCallback = (List<TflArrivalPrediction>) -> Unit
typealias NearbyStopsCallback = (TflStopPoints) -> Unit

interface TransportInfoApi {
    fun getNearbyStops(lat: Double, lon: Double, callback: NearbyStopsCallback)
    fun getArrivals(stopPoint: TflStopPoint, callback: ArrivalsCallback)
}

class TflApi(private val requestQueue: RequestQueue): TransportInfoApi {

    override fun getNearbyStops(lat: Double, lon: Double, callback: NearbyStopsCallback) {
        val endpoint = "/Place?type=NaptanMetroStation,NaptanRailStation,NaptanPublicBusCoachTram,NaptanFerryPort&lat=$lat&lon=$lon&radius=200"

        makeGetRequest(endpoint) { response ->
            val stopPoints = Gson().fromJson<TflStopPoints>(response, TflStopPoints::class.java)
            callback(stopPoints)
        }
    }

    override fun getArrivals(stopPoint: TflStopPoint, callback: ArrivalsCallback) {
        val endpoint = "/StopPoint/${stopPoint.naptanId}/Arrivals"

        makeGetRequest(endpoint) { response ->
            val responseModel = Gson().fromJson<List<TflArrivalPrediction>>(response, object : TypeToken<List<TflArrivalPrediction>>() {}.type)
            callback(responseModel)
        }
    }

    private fun makeGetRequest(endpoint: String, responseCallback: (String) -> Unit) {
        val url = BASE_URL + endpoint
        Log.i("GET", url)
        val request = StringRequest(Request.Method.GET,
                url,
                Response.Listener(responseCallback),
                Response.ErrorListener { Log.e("API request error", it.toString()) }
        )

        requestQueue.add(request)
    }
}