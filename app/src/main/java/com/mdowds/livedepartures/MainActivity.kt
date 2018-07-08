package com.mdowds.livedepartures

import android.location.Location
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.activity.WearableActivity
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import com.android.volley.RequestQueue
import com.google.android.gms.location.*


class MainActivity : WearableActivity() {

    private lateinit var adapter: ArrivalsRecyclerViewAdapter
    private lateinit var requestQueue: RequestQueue
    private lateinit var fusedLocationClient: FusedLocationProviderClient

//    val REQUEST_CHECK_SETTINGS = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()

        setUpRecyclerView()

        requestQueue = Volley.newRequestQueue(this)

        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) requestNearbyStops(location.latitude, location.longitude)
        }

//        val locationRequest = LocationRequest().apply {
//            interval = 10000
//            fastestInterval = 5000
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//
//        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
//        val client: SettingsClient = LocationServices.getSettingsClient(this)
//        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//
//        task.addOnSuccessListener { locationSettingsResponse ->
//        }
//
//        task.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException) {
//                // Location settings are not satisfied, but this can be fixed
//                // by showing the user a dialog.
//                try {
//                    // Show the dialog by calling startResolutionForResult(),
//                    // and check the result in onActivityResult().
//
//                    exception.startResolutionForResult(this@MainActivity,
//                            REQUEST_CHECK_SETTINGS)
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    // Ignore the error.
//                }
//            }
//        }
    }

    private fun requestNearbyStops(lat: Double, lon: Double) {
        val url = "https://api.tfl.gov.uk/Place?type=NaptanRailStation,NaptanPublicBusCoachTram&lat=$lat&lon=$lon&radius=200"

        val request = StringRequest(Request.Method.GET,
                url,
                Response.Listener { response ->
                    val responseModel = Gson().fromJson<TflStopPoints>(response, TflStopPoints::class.java)
                    requestArrivalInfo(responseModel.places.first())
                },
                Response.ErrorListener { Log.e("API request error", "That didn't work!") }
        )

        requestQueue.add(request)
    }

    private fun requestArrivalInfo(stopPoint: TflStopPoint) {
        val url = "https://api.tfl.gov.uk/StopPoint/${stopPoint.naptanId}/Arrivals"

        val request = StringRequest(Request.Method.GET,
                url,
                Response.Listener { response ->
                    val responseModel = Gson().fromJson<List<TflArrivalPrediction>>(response, object : TypeToken<List<TflArrivalPrediction>>() {}.type)
                    updateResults(responseModel, stopPoint.commonName)
                },
                Response.ErrorListener { Log.e("API request error", "That didn't work!") }
        )

        requestQueue.add(request)
    }

    private fun setUpRecyclerView() {
        adapter = ArrivalsRecyclerViewAdapter("Loading", listOf())
        arrivalsRecyclerView.adapter = adapter

        arrivalsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun updateResults(newResults: List<TflArrivalPrediction>, stopName: String) {
        val newModelsOrdered = newResults.sortedBy { it.timeToStation }
        val newItems = newModelsOrdered.map { ArrivalModel(it) }
        adapter.listItems = newItems
        adapter.stopName = stopName
        adapter.notifyDataSetChanged()
    }
}
