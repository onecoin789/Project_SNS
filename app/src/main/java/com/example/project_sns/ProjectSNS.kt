package com.example.project_sns

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ProjectSNS : Application() {
    companion object {
        private lateinit var application: ProjectSNS
        fun getInstance() : ProjectSNS = application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}