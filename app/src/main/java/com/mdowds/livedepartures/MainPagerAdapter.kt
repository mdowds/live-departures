package com.mdowds.livedepartures

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import com.mdowds.livedepartures.departurespage.DeparturesFragment
import com.mdowds.livedepartures.networking.TflStopPoints

class MainPagerAdapter(fm: FragmentManager,
                       dataSource: NearbyStopPointsDataSource,
                       private val container: ViewGroup) : FragmentStatePagerAdapter(fm) {

    init {
        dataSource.addObserver(this::stopPointsUpdated)
    }

    var modes = listOf<Mode>()
        private set

    override fun getItem(position: Int): Fragment {
        val modeToDisplay = modes[position]
        if(!modeToDisplay.canGetArrivals) return InactiveModeFragment()
        return DeparturesFragment().apply { mode = modeToDisplay }

    }

    override fun getCount(): Int = modes.count()

    override fun getPageTitle(position: Int): CharSequence? = modes[position].displayName

    private fun stopPointsUpdated(stopPoints: TflStopPoints) {
        startUpdate(container)

        modes = stopPoints.places
                .flatMap { it.modes }
                .asSequence()
                .distinct()
                .map { Mode.fromModeName(it) }
                .filterNotNull()
                .toList()

        notifyDataSetChanged()
        finishUpdate(container)
    }
}

