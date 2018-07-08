package com.mdowds.livedepartures

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.activity.WearableActivity
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : WearableActivity() {

    private lateinit var adapter: ArrivalsRecyclerViewAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var tflApi: TflApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()

        setUpRecyclerView()
        tflApi = TflApi(RequestQueueSingleton.getInstance(this.applicationContext).requestQueue)
        setUpLocationServices()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */)
    }


    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun setUpRecyclerView() {
        adapter = ArrivalsRecyclerViewAdapter("Loading", listOf())
        arrivalsRecyclerView.adapter = adapter

        arrivalsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setUpLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                tflApi.getNearbyStops(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude) {
                    requestArrivals(it)
                }
            }
        }
    }

    private fun requestArrivals(stopPoints: TflStopPoints) {
        tflApi.getArrivals(stopPoints.places.first(), this::updateResults)
    }

    private fun updateResults(newResults: List<TflArrivalPrediction>, stopName: String) {
        val newModelsOrdered = newResults.sortedBy { it.timeToStation }
        val newItems = newModelsOrdered.map { ArrivalModel(it) }
        adapter.listItems = newItems
        adapter.stopName = stopName
        adapter.notifyDataSetChanged()
    }
}
