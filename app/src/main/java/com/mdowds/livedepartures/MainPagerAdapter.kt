package com.mdowds.livedepartures

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val modes = listOf("Bus", "Tube", "Overground", "DLR")

    override fun getItem(position: Int): Fragment {
        return DeparturesFragment()
    }

    override fun getCount(): Int = modes.count()

    override fun getPageTitle(position: Int): CharSequence? = modes[position]

}