package com.xzt.demo.workmanagerdemo

import android.app.Application

/**
 *作者：created by zjt on 2021/1/12
 *描述:
 *
 */
class App :Application() {
    companion object {
    lateinit var instance: App
        private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}