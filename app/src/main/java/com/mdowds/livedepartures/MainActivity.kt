package com.mdowds.livedepartures

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.util.Log
import com.mdowds.livedepartures.utils.DevicePermissionsManager.Companion.PERMISSIONS_REQUEST_CODE

class MainActivity : FragmentActivity() {

    lateinit var dataSource: NearbyStopPointsDataSource
        private set

    private lateinit var pagerAdapter: MainPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataSource = NearbyStopPointsDataSource.create(this)
        pagerAdapter = MainPagerAdapter(supportFragmentManager)
        viewPager = findViewById(R.id.container)
        viewPager.adapter = pagerAdapter
    }

    override fun onResume() {
        super.onResume()
        dataSource.startUpdates()
    }

    override fun onPause() {
        super.onPause()
        dataSource.stopUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Log.i("Permissions result", "User interaction was cancelled.")
                (grantResults[0] == PERMISSION_GRANTED) -> dataSource.startUpdates()
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
}
