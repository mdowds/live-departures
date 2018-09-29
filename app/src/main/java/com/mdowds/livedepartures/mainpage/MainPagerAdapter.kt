package com.mdowds.livedepartures.mainpage

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.mdowds.livedepartures.departurespage.DeparturesFragment
import com.mdowds.livedepartures.departurespage.InactiveModeFragment

class MainPagerAdapter(fm: FragmentManager,
                       private val presenter: MainPresenter) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val modeToDisplay = presenter.modes[position]
        if(!modeToDisplay.canGetArrivals) return InactiveModeFragment()
        return DeparturesFragment().apply {
            mode = modeToDisplay
            allStopPoints = presenter.stopPoints
        }
    }

    override fun getCount(): Int = presenter.modes.count()

    override fun getPageTitle(position: Int): CharSequence? = presenter.modes[position].displayName
}

