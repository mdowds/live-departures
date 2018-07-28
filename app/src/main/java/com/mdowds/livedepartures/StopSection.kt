package com.mdowds.livedepartures

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.*
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.arrival_info.view.*
import kotlinx.android.synthetic.main.stop_name.view.*

class StopSection(val stopName: String, var arrivals: List<Arrival>) : Section(SectionParameters.builder()
        .itemResourceId(R.layout.arrival_info)
        .headerResourceId(R.layout.stop_name)
        .loadingResourceId(R.layout.arrivals_loading)
        .build()) {

    override fun getContentItemsTotal(): Int = arrivals.count()

    // TODO handle a null view holder
    override fun onBindHeaderViewHolder(holder: ViewHolder?) {
        (holder as StopNameViewHolder).run {
            setStopName(stopName)
            setNoDeparturesMessage(arrivals.count() == 0 && state == LOADED)
        }
    }

    // TODO handle a null view
    override fun getHeaderViewHolder(view: View?): ViewHolder = StopNameViewHolder(view!!)

    // TODO handle a null view
    override fun getItemViewHolder(view: View?): ViewHolder = ArrivalInfoViewHolder(view!!)

    // TODO handle a null view holder
    override fun onBindItemViewHolder(holder: ViewHolder?, position: Int) {
        (holder as ArrivalInfoViewHolder).bindDepartureInfoModel(arrivals[position])
    }

    class StopNameViewHolder(private var view: View) : ViewHolder(view) {
        fun setStopName(stopName: String) {
            view.stop_name.text = stopName
        }

        fun setNoDeparturesMessage(isNoDepartures: Boolean) {
            view.no_departures_text.visibility = if(isNoDepartures) View.VISIBLE else View.GONE
        }
    }

    class ArrivalInfoViewHolder(private val view: View) : ViewHolder(view) {

        fun bindDepartureInfoModel(arrival: Arrival) {
            view.route_name.text = arrival.line
            view.route_destination.text = arrival.destination
            view.arrival_time.text = arrival.arrivalTime
        }
    }
}