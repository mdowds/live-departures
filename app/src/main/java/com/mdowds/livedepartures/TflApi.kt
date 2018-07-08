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

class TflApi(private val requestQueue: RequestQueue, private val arrivalsCallback: ArrivalsCallback) {

    fun getNearbyStops(lat: Double, lon: Double) {
        val endpoint = "/Place?type=NaptanRailStation,NaptanPublicBusCoachTram&lat=$lat&lon=$lon&radius=200"

        makeGetRequest(endpoint) { response ->
            val responseModel = Gson().fromJson<TflStopPoints>(response, TflStopPoints::class.java)
            getArrivals(responseModel.places.first())
        }
    }

    private fun getArrivals(stopPoint: TflStopPoint) {
        val endpoint = "/StopPoint/${stopPoint.naptanId}/Arrivals"

        makeGetRequest(endpoint) { response ->
            val responseModel = Gson().fromJson<List<TflArrivalPrediction>>(response, object : TypeToken<List<TflArrivalPrediction>>() {}.type)
            arrivalsCallback(responseModel, stopPoint.commonName)
        }
    }

    private fun makeGetRequest(endpoint: String, responseCallback: (String) -> Unit) {
        val request = StringRequest(Request.Method.GET,
                BASE_URL + endpoint,
                Response.Listener(responseCallback),
                Response.ErrorListener { error -> Log.e("API request error", error.toString()) }
        )

        requestQueue.add(request)
    }
}