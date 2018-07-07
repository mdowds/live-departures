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

class MainActivity : WearableActivity() {

    private lateinit var models: List<ListModel>
    private lateinit var adapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()

//        recyclerView.isEdgeItemsCenteringEnabled = true
//        recyclerView.layoutManager = WearableLinearLayoutManager(this)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val queue = Volley.newRequestQueue(this)
        val url = "https://api.tfl.gov.uk/StopPoint/910GWLTHQRD/Arrivals"
        models = listOf()

        val request = StringRequest(Request.Method.GET,
                url,
                Response.Listener { response ->
                    val responseModel = Gson().fromJson<List<ArrivalPrediction>>(response, object : TypeToken<List<ArrivalPrediction>>() {}.type)
                    Log.i("API response", responseModel.toString())
                    updateListItems(responseModel)
                },
                Response.ErrorListener { print("That didn't work!") }
        )

        queue.add(request)

        adapter = RecyclerViewAdapter(models)
        recyclerView.adapter = adapter
    }

    private fun updateListItems(newModels: List<ArrivalPrediction>) {
        val newModelsOrdered = newModels.sortedBy{ it.timeToStation }
        val newItems = newModelsOrdered.map { ListModel(it.lineName, it.towards, "${it.timeToStation} secs") }
        adapter.listItems = newItems
        adapter.notifyDataSetChanged()
    }
}
