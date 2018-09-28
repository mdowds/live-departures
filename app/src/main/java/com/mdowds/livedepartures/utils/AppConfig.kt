package com.mdowds.livedepartures.utils

import android.content.res.Resources
import com.google.gson.Gson
import com.mdowds.livedepartures.R
import java.io.InputStreamReader

class AppConfig(private val resources: Resources) {

    val config: Config by lazy {
        val configFile = resources.openRawResource(R.raw.config)
        val reader = InputStreamReader(configFile)
        Gson().fromJson<Config>(reader, Config::class.java)
    }
}

data class Config(val stopsToShow: Int,
                  val departuresPerStop: Int,
                  val departuresRefreshInSecs: Int,
                  val distanceToFetchNewStopsInMetres: Int,
                  val radiusToFetchStopsInMetres: Int)