package com.mdowds.livedepartures.departurespage

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.view.View.*
import com.mdowds.livedepartures.Departure
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.R
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.Section.State.*
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.departure_info.view.*
import kotlinx.android.synthetic.main.stop_name.view.*

class StopSection(private val stopName: String,
                  private val indicator: String?,
                  var departures: List<Departure>) : Section(SectionParameters.builder()
        .itemResourceId(R.layout.departure_info)
        .headerResourceId(R.layout.stop_name)
        .loadingResourceId(R.layout.departures_loading)
        .build()) {

    override fun getContentItemsTotal(): Int = departures.count()

    // TODO handle a null view holder
    override fun onBindHeaderViewHolder(holder: ViewHolder?) {
        (holder as StopNameViewHolder).run {
            setStopName(stopName)
            setStopIndicator(indicator)
            setNoDeparturesMessage(departures.count() == 0 && state == LOADED)
        }
    }

    // TODO handle a null view
    override fun getHeaderViewHolder(view: View?): ViewHolder = StopNameViewHolder(view!!)

    // TODO handle a null view
    override fun getItemViewHolder(view: View?): ViewHolder = DepartureInfoViewHolder(view!!)

    // TODO handle a null view holder
    override fun onBindItemViewHolder(holder: ViewHolder?, position: Int) {
        (holder as DepartureInfoViewHolder).bindDepartureInfoModel(departures[position])
    }

    class StopNameViewHolder(private var view: View) : ViewHolder(view) {
        fun setStopName(stopName: String) {
            view.stop_name.text = stopName
        }

        fun setStopIndicator(stopIndicator: String?) {
            if(stopIndicator.isNullOrEmpty()) {
                view.stop_indicator.visibility = GONE
            } else {
                view.stop_indicator.text = stopIndicator
            }
        }

        fun setNoDeparturesMessage(isNoDepartures: Boolean) {
            view.no_departures_text.visibility = if(isNoDepartures) VISIBLE else GONE
        }
    }

    class DepartureInfoViewHolder(private val view: View) : ViewHolder(view) {

        fun bindDepartureInfoModel(departure: Departure) {

            val title = when(departure.mode) {
                Mode.Tube -> "${departure.line} - ${departure.direction}"
                Mode.Overground, Mode.TflRail, Mode.DLR -> departure.platform
                else -> departure.line
            }

            view.title.text = title
            view.subtitle.text = if(departure.isTerminating) "Arriving"
                else departure.destination
            view.arrival_time.text = departure.departureTime
        }
    }
}