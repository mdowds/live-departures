package com.mdowds.livedepartures

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.arrival_info.view.*
import kotlinx.android.synthetic.main.stop_name.view.*

class ArrivalsRecyclerViewAdapter(var stopName: String, var listItems: List<ArrivalModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType(val rawValue: Int) {
        STOP_NAME(0), ARRIVAL_INFO(1)
    }

    override fun getItemCount(): Int = listItems.count() + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            ViewType.STOP_NAME.rawValue -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.stop_name, parent, false)
                StopNameViewHolder(view)
            }
            ViewType.ARRIVAL_INFO.rawValue -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.arrival_info, parent, false)
                ArrivalInfoViewHolder(view)
            }
            else -> throw RuntimeException("there is no type that matches the type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ArrivalInfoViewHolder -> holder.bindDepartureInfoModel(listItems[position - 1])
            is StopNameViewHolder -> holder.setStopName(stopName)
        }
    }

    override fun getItemViewType(position: Int): Int =
            if(position == 0) ViewType.STOP_NAME.rawValue else ViewType.ARRIVAL_INFO.rawValue

    class StopNameViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {
        fun setStopName(stopName: String) {
            view.stop_name.text = stopName
        }
    }

    class ArrivalInfoViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {

        fun bindDepartureInfoModel(arrivalModel: ArrivalModel) {
            view.route_name.text = arrivalModel.line
            view.route_destination.text = arrivalModel.destination
            view.arrival_time.text = arrivalModel.arrivalTime
        }
    }
}

