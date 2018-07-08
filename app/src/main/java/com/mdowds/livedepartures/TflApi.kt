package com.mdowds.livedepartures

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val BASE_URL = "https://api.tfl.gov.uk"

typealias ArrivalsCallback = (List<TflArrivalPrediction>, String) -> Unit
typealias NearbyStopsCallback = (TflStopPoints) -> Unit

class TflApi(private val requestQueue: RequestQueue) {

    fun getNearbyStops(lat: Double, lon: Double, callback: NearbyStopsCallback) {
        val endpoint = "/Place?type=NaptanRailStation,NaptanPublicBusCoachTram&lat=$lat&lon=$lon&radius=200"

        makeGetRequest(endpoint) { response ->
            val stopPoints = Gson().fromJson<TflStopPoints>(response, TflStopPoints::class.java)
            callback(stopPoints)
        }
    }

    fun getArrivals(stopPoint: TflStopPoint, callback: ArrivalsCallback) {
        val endpoint = "/StopPoint/${stopPoint.naptanId}/Arrivals"

        makeGetRequest(endpoint) { response ->
            val responseModel = Gson().fromJson<List<TflArrivalPrediction>>(response, object : TypeToken<List<TflArrivalPrediction>>() {}.type)
            callback(responseModel, stopPoint.commonName)
        }
    }

    private fun makeGetRequest(endpoint: String, responseCallback: (String) -> Unit) {
        val request = StringRequest(Request.Method.GET,
                BASE_URL + endpoint,
                Response.Listener(responseCallback),
                Response.ErrorListener { Log.e("API request error", it.toString()) }
        )

        requestQueue.add(request)
    }
}