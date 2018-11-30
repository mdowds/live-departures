package com.mdowds.livedepartures.departurespage

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mdowds.livedepartures.*
import com.mdowds.livedepartures.mainpage.MainActivity
import com.mdowds.livedepartures.networking.model.TflStopPoint
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.LOADED
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.LOADING
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.departures_fragment.*

interface DeparturesView {
    fun addStopSection(stopPoint: StopPoint): StopSection
    fun removeStopSections()
    fun updateResults(newDepartures: List<Departure>, stopPoint: StopPoint)
}

class DeparturesFragment : Fragment(), DeparturesView {

    var mode: Mode? = null
    lateinit var allStopPoints: List<TflStopPoint>

    private lateinit var presenter: DeparturesPresenter
    private lateinit var adapter: SectionedRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.departures_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpRecyclerView()
        presenter = DeparturesPresenter.create(this, (activity as MainActivity).arrivalsDataSource, allStopPoints)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun addStopSection(stopPoint: StopPoint): StopSection {
        val stopSection = StopSection(stopPoint.name, stopPoint.indicator, listOf())
        stopSection.state = LOADING
        adapter.addSection(stopPoint.stopId, stopSection)
        adapter.notifyDataSetChanged()
        return stopSection
    }

    override fun removeStopSections() = adapter.removeAllSections()

    override fun updateResults(newDepartures: List<Departure>, stopPoint: StopPoint) {
        val section = adapter.getSection(stopPoint.stopId)
        (section as StopSection).departures = newDepartures
        section.state = LOADED
        adapter.notifyDataSetChanged()
    }

    private fun setUpRecyclerView() {
        adapter = SectionedRecyclerViewAdapter()
        departuresRecyclerView.adapter = adapter
        departuresRecyclerView.layoutManager = LinearLayoutManager(context)
    }
}
