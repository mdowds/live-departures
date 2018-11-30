package com.mdowds.livedepartures

import com.mdowds.livedepartures.mainpage.MainActivity
import com.mdowds.livedepartures.networking.*
import com.mdowds.livedepartures.networking.model.TflArrivalPrediction
import com.mdowds.livedepartures.networking.model.TflStopPoint
import com.mdowds.livedepartures.utils.AppConfig
import com.mdowds.livedepartures.utils.Config
import com.mdowds.livedepartures.utils.Observable
import java.util.*

typealias ArrivalsResponse = Pair<TflStopPoint, List<TflArrivalPrediction>>

class ArrivalsDataSource(private val api: TransportInfoApi,
                         private val config: Config,
                         private val arrivalRequestsTimer: Timer): Observable<ArrivalsResponse>() {

    companion object {
        fun create(view: MainActivity): ArrivalsDataSource {

            val config = AppConfig(view.resources).config

            return ArrivalsDataSource(TflApi(RequestQueueSingleton.getInstance(view.applicationContext).requestQueue), config, Timer("Arrival requests"))
        }
    }

    private val requestArrivalsFor = mutableListOf<TflStopPoint>()

    fun startUpdates() {
        val repeatedTask = object : TimerTask() {
            override fun run() = requestArrivalsForAll()
        }

        val period = (config.departuresRefreshInSecs * 1000).toLong()
        arrivalRequestsTimer.scheduleAtFixedRate(repeatedTask, 0L, period)
    }

    fun stopUpdates() = arrivalRequestsTimer.purge()

    fun addStopPoint(stopPoint: TflStopPoint) {
        requestArrivals(stopPoint)
        if(!requestArrivalsFor.contains(stopPoint)) requestArrivalsFor.add(stopPoint)
    }

    fun removeStopPoints() = requestArrivalsFor.clear()

    fun requestArrivalsForAll() = requestArrivalsFor.forEach(this::requestArrivals)

    fun onArrivalsResponse(stopPoint: TflStopPoint, arrivals: List<TflArrivalPrediction>) {
        if(arrivals.isEmpty()) requestArrivalsFor.remove(stopPoint)
        notifyObservers(Pair(stopPoint, arrivals))
    }

    private fun requestArrivals(stopPoint: TflStopPoint) {
        api.getArrivals(stopPoint, { arrivals ->
            onArrivalsResponse(stopPoint, arrivals)
        })
    }
}