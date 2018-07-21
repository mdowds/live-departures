package com.mdowds.livedepartures

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.activity.WearableActivity
import com.google.android.gms.location.*
import com.mdowds.livedepartures.networking.*
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.*
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : WearableActivity() {

    private lateinit var adapter: SectionedRecyclerViewAdapter
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
        // TODO overall loading state for location and stops fetches
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
        adapter = SectionedRecyclerViewAdapter()
        arrivalsRecyclerView.adapter = adapter

        arrivalsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setUpLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                tflApi.getNearbyStops(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude) {
                    createSections(it)
                }
            }
        }
    }

    private fun createSections(stopPoints: TflStopPoints) {
        stopPoints.places.take(5).forEach {
            val stopSection = StopSection(it.commonName, listOf())
            stopSection.state = LOADING
            adapter.addSection(stopSection)
            adapter.notifyDataSetChanged()
            requestArrivals(it, stopSection)
        }
    }

    private fun requestArrivals(stopPoint: TflStopPoint, section: StopSection) {
        tflApi.getArrivals(stopPoint) {
            updateResults(it, section)
        }
    }

    private fun updateResults(newResults: List<TflArrivalPrediction>, section: StopSection) {
        val newModelsOrdered = newResults.sortedBy { it.timeToStation }
        val newItems = newModelsOrdered.take(5).map { ArrivalModel(it) }
        section.arrivals = newItems
        section.state = LOADED
        adapter.notifyDataSetChanged()
    }
}
