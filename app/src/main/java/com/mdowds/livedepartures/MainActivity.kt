package com.mdowds.livedepartures


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager

class MainActivity : FragmentActivity() {

    private lateinit var departuresPagerAdapter: DeparturesPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        departuresPagerAdapter = DeparturesPagerAdapter(supportFragmentManager)
        viewPager = findViewById(R.id.container)
        viewPager.adapter = departuresPagerAdapter
    }

    class DeparturesPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return DeparturesFragment()
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "SECTION 1"
                1 -> return "SECTION 2"
                2 -> return "SECTION 3"
            }
            return null
        }
    }
}
