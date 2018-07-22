package com.mdowds.livedepartures

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.activity.WearableActivity
import com.mdowds.livedepartures.networking.*
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.*
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*

interface ArrivalsView {
    fun addStopSection(stopPoint: TflStopPoint): StopSection
    fun updateResults(newArrivals: List<ArrivalModel>, section: Section)
}

class ArrivalsActivity : WearableActivity(), ArrivalsView {

    private lateinit var presenter: ArrivalsPresenter
    private lateinit var adapter: SectionedRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    override fun updateResults(newArrivals: List<ArrivalModel>, section: Section) {
        (section as StopSection).arrivals = newArrivals
        section.state = LOADED
        adapter.notifyDataSetChanged()
    }

    private fun setUpRecyclerView() {
        adapter = SectionedRecyclerViewAdapter()
        arrivalsRecyclerView.adapter = adapter
        arrivalsRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}
