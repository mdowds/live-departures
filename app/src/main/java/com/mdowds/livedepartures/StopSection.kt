package com.mdowds.livedepartures

import android.support.v7.widget.RecyclerView
import android.view.View
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.arrival_info.view.*
import kotlinx.android.synthetic.main.stop_name.view.*

class StopSection(val stopName: String, var arrivals: List<ArrivalModel>) : Section(SectionParameters.builder()
        .itemResourceId(R.layout.arrival_info)
        .headerResourceId(R.layout.stop_name)
        .loadingResourceId(R.layout.arrivals_loading)
        .build()) {

    override fun getContentItemsTotal(): Int = arrivals.count()

    // TODO handle a null view holder
    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        (holder as StopNameViewHolder).setStopName(stopName)
    }

    // TODO handle a null view
    override fun getHeaderViewHolder(view: View?): RecyclerView.ViewHolder {
        return StopNameViewHolder(view!!)
    }

    // TODO handle a null view
    override fun getItemViewHolder(view: View?): RecyclerView.ViewHolder {
        return ArrivalInfoViewHolder(view!!)
    }

    // TODO handle a null view holder
    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ArrivalInfoViewHolder).bindDepartureInfoModel(arrivals[position])
    }

    class StopNameViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {
        fun setStopName(stopName: String) {
            view.stop_name.text = stopName
        }
    }

    class ArrivalInfoViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bindDepartureInfoModel(arrivalModel: ArrivalModel) {
            view.route_name.text = arrivalModel.line
            view.route_destination.text = arrivalModel.destination
            view.arrival_time.text = arrivalModel.arrivalTime
        }
    }
}