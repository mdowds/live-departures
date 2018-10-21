package com.mdowds.livedepartures.utils

import android.content.res.Resources
import com.mdowds.livedepartures.R
import java.util.*

class AppConfig(private val resources: Resources) {

    val config: Config by lazy {
        val configFile = resources.openRawResource(R.raw.config)
        val properties = Properties()
        properties.load(configFile)
        Config(properties)
    }
}

data class Config(val stopsToShow: Int,
                  val departuresPerStop: Int,
                  val departuresRefreshInSecs: Int,
                  val distanceToFetchNewStopsInMetres: Int,
                  val radiusToFetchStopsInMetres: Int) {

    constructor(properties: Properties) : this(properties.getProperty("stopsToShow").toInt(),
                properties.getProperty("departuresPerStop").toInt(),
                properties.getProperty("departuresRefreshInSecs").toInt(),
                properties.getProperty("distanceToFetchNewStopsInMetres").toInt(),
                properties.getProperty("radiusToFetchStopsInMetres").toInt()
                )
}