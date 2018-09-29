package com.mdowds.livedepartures.mainpage

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import com.mdowds.livedepartures.ArrivalsDataSource
import com.mdowds.livedepartures.Mode
import com.mdowds.livedepartures.NearbyStopPointsDataSource
import com.mdowds.livedepartures.R
import com.mdowds.livedepartures.utils.DevicePermissionsManager.Companion.PERMISSIONS_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_main.*

interface MainView {
    fun showLoadingSpinner()
    fun hideLoadingSpinner()
    fun setHeaderTextColor(color: Int)
    fun setHeaderBackgroundColor(color: Int)
//    fun updateModes(modes: List<Mode>)
    fun refreshStopPoints()
}

class MainActivity : FragmentActivity(), MainView {

    lateinit var stopPointsDataSource: NearbyStopPointsDataSource
        private set

    lateinit var arrivalsDataSource: ArrivalsDataSource
        private set

    private lateinit var presenter: MainPresenter
//    private lateinit var pagerAdapter: MainPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stopPointsDataSource = NearbyStopPointsDataSource.create(this)
        arrivalsDataSource = ArrivalsDataSource.create(this)
        presenter = MainPresenter(this, stopPointsDataSource, arrivalsDataSource)
        setUpViewPager()
        showLoadingSpinner()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun showLoadingSpinner() {
        fullScreenProgressBar.visibility = View.VISIBLE
        viewPager.visibility = View.GONE
    }

    override fun hideLoadingSpinner() {
        fullScreenProgressBar.visibility = View.GONE
        viewPager.visibility = View.VISIBLE
    }

    override fun setHeaderTextColor(color: Int) {
        pager_header.setTextColor(color)
        pager_header.tabIndicatorColor = color
    }

    override fun setHeaderBackgroundColor(color: Int) {
        pager_header.setBackgroundColor(color)
    }

    override fun refreshStopPoints() {
        createPagerAdapter(viewPager)
        pager_header.setBackgroundColor(presenter.modes.first().color)
    }

//    override fun updateModes(modes: List<Mode>) {
//        pagerAdapter.startUpdate(viewPager)
//        pagerAdapter.notifyDataSetChanged()
//        pagerAdapter.finishUpdate(viewPager)
//        pager_header.setBackgroundColor(modes.first().color)
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Log.i("Permissions result", "User interaction was cancelled.")
                (grantResults[0] == PERMISSION_GRANTED) -> stopPointsDataSource.startUpdates()
                else -> {
                    // TODO show message with link to settings if permission rejected
//                    showSnackbar(R.string.permission_denied_explanation, R.string.settings,
//                            View.OnClickListener {
//                                // Build intent that displays the App settings screen.
//                                val intent = Intent().apply {
//                                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                                    data = Uri.fromParts("package", APPLICATION_ID, null)
//                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                }
//                                startActivity(intent)
//                            })
                }
            }
        }
    }

    private fun setUpViewPager() {
        viewPager = findViewById(R.id.container)
        createPagerAdapter(viewPager)
        viewPager.addOnPageChangeListener(presenter)
    }

    private fun createPagerAdapter(viewPager: ViewPager) {
        val pagerAdapter = MainPagerAdapter(supportFragmentManager, presenter)
        viewPager.adapter = pagerAdapter
    }
}
