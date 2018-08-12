package com.mdowds.livedepartures

import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.mdowds.livedepartures.departurespage.DeparturesFragment

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val modes = Mode.values()

    override fun getItem(position: Int): Fragment =
            DeparturesFragment().apply { mode = modes[position] }

    override fun getCount(): Int = modes.count()

    override fun getPageTitle(position: Int): CharSequence? = modes[position].name
}

enum class Mode(val tflName: String, val color: Int) {
    Bus("bus", Color.rgb(220, 36, 31)),
    Tube("tube", Color.rgb(0, 25, 168)),
    Overground("overground", Color.rgb(239, 123, 16));
}
