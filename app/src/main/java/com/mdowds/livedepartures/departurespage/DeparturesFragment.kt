package com.mdowds.livedepartures.departurespage

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mdowds.livedepartures.*
import com.mdowds.livedepartures.mainpage.MainActivity
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.LOADED
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.LOADING
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.departures_fragment.*

interface DeparturesView {
    fun addStopSection(stopPoint: StopPoint): StopSection
    fun removeStopSections()
    fun updateResults(newDepartures: List<Departure>, section: Section)
    fun showLoadingSpinner()
    fun hideLoadingSpinner()
}

class DeparturesFragment : Fragment(), DeparturesView {

    var mode: Mode? = null

    private lateinit var presenter: DeparturesPresenter
    private lateinit var adapter: SectionedRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.departures_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpRecyclerView()
        presenter = DeparturesPresenter.create(this, (activity as MainActivity).dataSource)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun addStopSection(stopPoint: StopPoint): StopSection {
        val stopSection = StopSection(stopPoint.name, stopPoint.indicator, listOf())
        stopSection.state = LOADING
        adapter.addSection(stopSection)
        adapter.notifyDataSetChanged()
        return stopSection
    }

    override fun removeStopSections() = adapter.removeAllSections()

    override fun updateResults(newDepartures: List<Departure>, section: Section) {
        (section as StopSection).departures = newDepartures
        section.state = LOADED
        adapter.notifyDataSetChanged()
    }

    override fun showLoadingSpinner() {
        pageProgressBar.visibility = View.VISIBLE
        departuresRecyclerView.visibility = View.GONE
    }

    override fun hideLoadingSpinner() {
        pageProgressBar.visibility = View.GONE
        departuresRecyclerView.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        adapter = SectionedRecyclerViewAdapter()
        departuresRecyclerView.adapter = adapter
        departuresRecyclerView.layoutManager = LinearLayoutManager(context)
    }
}
