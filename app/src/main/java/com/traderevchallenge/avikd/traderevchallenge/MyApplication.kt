package com.traderevchallenge.avikd.traderevchallenge

import android.app.Application
import android.content.Context
import com.traderevchallenge.avikd.traderevchallenge.modules.AppComponent
import com.traderevchallenge.avikd.traderevchallenge.modules.AppModule
import com.traderevchallenge.avikd.traderevchallenge.modules.DaggerAppComponent
import com.traderevchallenge.avikd.traderevchallenge.modules.UtilsModule


class MyApplication : Application() {
    lateinit var appComponent: AppComponent
    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = this
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).utilsModule(UtilsModule()).build()
    }

    protected override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
    }
}