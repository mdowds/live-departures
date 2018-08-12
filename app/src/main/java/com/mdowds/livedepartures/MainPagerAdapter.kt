package com.mdowds.livedepartures

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.mdowds.livedepartures.departurespage.DeparturesFragment

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    // TODO update this to only show active modes when StopPoints updates
    private val modes = Mode.values()

    override fun getItem(position: Int): Fragment =
            DeparturesFragment().apply { mode = modes[position] }

    override fun getCount(): Int = modes.count()

    override fun getPageTitle(position: Int): CharSequence? = modes[position].name
}

