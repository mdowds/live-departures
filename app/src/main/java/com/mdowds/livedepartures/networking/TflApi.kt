package com.mdowds.livedepartures.networking

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mdowds.livedepartures.BuildConfig
import com.mdowds.livedepartures.networking.model.TflArrivalPrediction
import com.mdowds.livedepartures.networking.model.TflStopPoint
import com.mdowds.livedepartures.networking.model.TflStopPoints

private const val BASE_URL = "https://api.tfl.gov.uk"

typealias ArrivalsCallback = (List<TflArrivalPrediction>) -> Unit
typealias NearbyStopsCallback = (TflStopPoints) -> Unit
typealias ErrorCallback = (VolleyError) -> Unit

interface TransportInfoApi {
    fun getNearbyStops(lat: Double, lon: Double, radius: Int, callback: NearbyStopsCallback, errorCallback: ErrorCallback? = null)
    fun getArrivals(stopPoint: TflStopPoint, callback: ArrivalsCallback, errorCallback: ErrorCallback? = null)
}

class TflApi(private val requestQueue: RequestQueue): TransportInfoApi {

    private val appId = BuildConfig.TFL_APP_ID
    private val appKey = BuildConfig.TFL_APP_KEY

    private val tflStopTypes = "NaptanMetroStation,NaptanRailStation,NaptanPublicBusCoachTram,NaptanFerryPort"

    override fun getNearbyStops(lat: Double, lon: Double, radius: Int, callback: NearbyStopsCallback, errorCallback: ErrorCallback?) {
        val endpoint = "/Place?type=$tflStopTypes&lat=$lat&lon=$lon&radius=$radius"

        makeGetRequest(endpoint, { response ->
            val stopPoints = Gson().fromJson<TflStopPoints>(response, TflStopPoints::class.java)
            callback(stopPoints)
        }, errorCallback)
    }

    override fun getArrivals(stopPoint: TflStopPoint, callback: ArrivalsCallback, errorCallback: ErrorCallback?) {
        val endpoint = "/StopPoint/${stopPoint.naptanId}/Arrivals"

        makeGetRequest(endpoint, { response ->
            val responseModel = Gson().fromJson<List<TflArrivalPrediction>>(response, object : TypeToken<List<TflArrivalPrediction>>() {}.type)
            callback(responseModel)
        }, errorCallback)
    }

    private fun makeGetRequest(endpoint: String, responseCallback: (String) -> Unit, errorCallback: ErrorCallback?) {
        val separator = if(endpoint.contains("?")) "&" else "?"
        val url = BASE_URL + endpoint + separator + "app_id=$appId&app_key=$appKey"
        Log.i("GET", url)
        val request = StringRequest(Request.Method.GET,
                url,
                Response.Listener(responseCallback),
                Response.ErrorListener {
                    Log.e("API request error", it.toString())
                    if(errorCallback != null) errorCallback(it)
                }
        )

        requestQueue.add(request)
    }
}