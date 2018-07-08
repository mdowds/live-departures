package com.mdowds.livedepartures

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.arrival_info.view.*

class ArrivalsRecyclerViewAdapter(var listItems: List<ArrivalInfoModel>) : RecyclerView.Adapter<ArrivalsRecyclerViewAdapter.ViewHolder>()  {
    override fun getItemCount(): Int = listItems.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.arrival_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDepartureInfoModel(listItems[position])
    }

    class ViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {

        fun bindDepartureInfoModel(arrivalInfoModel: ArrivalInfoModel) {
            view.route_name.text = arrivalInfoModel.line
            view.route_destination.text = arrivalInfoModel.destination
            view.arrival_time.text = arrivalInfoModel.arrivalTime
        }
    }
}

data class ArrivalInfoModel(val line: String, val destination: String, val arrivalTime: String)
