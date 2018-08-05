package com.mdowds.livedepartures

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.LOADED
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.LOADING
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.arrivals_fragment.*

interface ArrivalsView {
    fun addStopSection(stopPoint: StopPoint): StopSection
    fun removeStopSections()
    fun updateResults(newArrivals: List<Arrival>, section: Section)
    fun showLoadingSpinner()
    fun hideLoadingSpinner()
}

class ArrivalsFragment : Fragment(), ArrivalsView {

    private lateinit var presenter: ArrivalsPresenter
    private lateinit var adapter: SectionedRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.arrivals_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

    override fun addStopSection(stopPoint: StopPoint): StopSection {
        val stopSection = StopSection(stopPoint.name, stopPoint.indicator, listOf())
        stopSection.state = LOADING
        adapter.addSection(stopSection)
        adapter.notifyDataSetChanged()
        return stopSection
    }

    override fun removeStopSections() = adapter.removeAllSections()

    override fun updateResults(newArrivals: List<Arrival>, section: Section) {
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
        arrivalsRecyclerView.layoutManager = LinearLayoutManager(context)
    }
}
