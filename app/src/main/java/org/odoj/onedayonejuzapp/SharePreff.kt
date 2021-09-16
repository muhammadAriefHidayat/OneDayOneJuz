package org.odoj.onedayonejuzapp

import android.app.Application

class SharePreff:Application() {
    override fun onCreate() {
        super.onCreate()
        Appreff.init(this)
    }
}