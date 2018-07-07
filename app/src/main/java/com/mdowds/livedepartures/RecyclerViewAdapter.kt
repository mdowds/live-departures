package com.mdowds.livedepartures

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*

class RecyclerViewAdapter(var listItems: List<ListModel>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>()  {
    override fun getItemCount(): Int = listItems.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindListModel(listItems[position])
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        fun bindListModel(listModel: ListModel) {
            view.list_item_title.text = listModel.title
            view.list_item_subtitle.text = listModel.subtitle
            view.list_item_detail.text = listModel.detail
        }
    }
}

data class ListModel(val title: String, val subtitle: String, val detail: String)
