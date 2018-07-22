package com.mdowds.livedepartures

import android.app.job.JobParameters
import android.app.job.JobService

class UpdateArrivalsJobService: JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        // Kick off the API request and call jobFinished() when it calls back
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // What to do when the job is unexpectedly cancelled
        // Return true to reschedule and false to not
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}