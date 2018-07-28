package com.mdowds.livedepartures

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.activity.WearableActivity
import android.view.View
import com.mdowds.livedepartures.networking.*
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.*
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.arrivals_activity.*

interface ArrivalsView {
    fun addStopSection(stopPoint: TflStopPoint): StopSection
    fun removeStopSections()
    fun updateResults(newArrivals: List<ArrivalModel>, section: Section)
    fun showLoadingSpinner()
    fun hideLoadingSpinner()
}

class ArrivalsActivity : WearableActivity(), ArrivalsView {

    private lateinit var presenter: ArrivalsPresenter
    private lateinit var adapter: SectionedRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.arrivals_activity)
        setAmbientEnabled()

        presenter = ArrivalsPresenter.create(this)
        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun addStopSection(stopPoint: TflStopPoint): StopSection {
        val stopSection = StopSection(stopPoint.commonName, listOf())
        stopSection.state = LOADING
        adapter.addSection(stopSection)
        adapter.notifyDataSetChanged()
        return stopSection
    }

    override fun removeStopSections() = adapter.removeAllSections()

    override fun updateResults(newArrivals: List<ArrivalModel>, section: Section) {
        (section as StopSection).arrivals = newArrivals
        section.state = LOADED
        adapter.notifyDataSetChanged()
    }

    override fun showLoadingSpinner() {
        fullScreenProgressBar.visibility = View.VISIBLE
        arrivalsRecyclerView.visibility = View.GONE
    }

    override fun hideLoadingSpinner() {
        fullScreenProgressBar.visibility = View.GONE
        arrivalsRecyclerView.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        presenter.onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun setUpRecyclerView() {
        adapter = SectionedRecyclerViewAdapter()
        arrivalsRecyclerView.adapter = adapter
        arrivalsRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}
