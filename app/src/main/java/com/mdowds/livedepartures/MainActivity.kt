package com.mdowds.livedepartures

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.activity.WearableActivity
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.HORIZONTAL


class MainActivity : WearableActivity() {

    private lateinit var models: List<ArrivalInfoModel>
    private lateinit var adapter: ArrivalsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()

        arrivalsRecyclerView.layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, HORIZONTAL)
        arrivalsRecyclerView.addItemDecoration(divider)

        val queue = Volley.newRequestQueue(this)
        val url = "https://api.tfl.gov.uk/StopPoint/910GWLTHQRD/Arrivals"
        models = listOf()

        val request = StringRequest(Request.Method.GET,
                url,
                Response.Listener { response ->
                    val responseModel = Gson().fromJson<List<TflArrivalPrediction>>(response, object : TypeToken<List<TflArrivalPrediction>>() {}.type)
                    updateListItems(responseModel)
                },
                Response.ErrorListener { Log.e("API request error", "That didn't work!") }
        )

        queue.add(request)

        adapter = ArrivalsRecyclerViewAdapter(models)
        arrivalsRecyclerView.adapter = adapter
    }

    private fun updateListItems(newModels: List<TflArrivalPrediction>) {
        val newModelsOrdered = newModels.sortedBy{ it.timeToStation }
        val newItems = newModelsOrdered.map { ArrivalInfoModel(it) }
        adapter.listItems = newItems
        adapter.notifyDataSetChanged()
    }
}
