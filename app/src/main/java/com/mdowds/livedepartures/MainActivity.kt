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
    private lateinit var tflApi: TflApi
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()

        setUpRecyclerView()
        tflApi = TflApi(RequestQueueSingleton.getInstance(this.applicationContext).requestQueue)
        locationManager = LocationManager(this)
    }

    override fun onResume() {
        // TODO overall loading state for location and stops fetches
        super.onResume()
        locationManager.startLocationUpdates {
            tflApi.getNearbyStops(it.latitude, it.longitude, this::createSections)
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.stopLocationUpdates()
    }

    private fun setUpRecyclerView() {
        adapter = SectionedRecyclerViewAdapter()
        arrivalsRecyclerView.adapter = adapter

        arrivalsRecyclerView.layoutManager = LinearLayoutManager(this)
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
