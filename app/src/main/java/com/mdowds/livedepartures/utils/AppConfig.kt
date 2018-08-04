package com.mdowds.livedepartures.utils

import android.content.res.Resources
import com.google.gson.Gson
import com.mdowds.livedepartures.R
import java.io.InputStreamReader

// TODO find better, non-singleton way to do this
object AppConfig {

    var resources: Resources? = null

    val config: Config by lazy {
        if (resources != null) {
            val configFile = resources!!.openRawResource(R.raw.config)
            val reader = InputStreamReader(configFile)
            Gson().fromJson<Config>(reader, Config::class.java)
        } else throw Exception("Resources not set")
    }
}

data class Config(val useFakeLocation: Boolean, val fakeLocation: String)