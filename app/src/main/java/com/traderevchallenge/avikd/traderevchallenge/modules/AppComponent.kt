package com.traderevchallenge.avikd.traderevchallenge.modules

import com.traderevchallenge.avikd.traderevchallenge.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * AppComponent
 * Component classes for DI with Dagger2. Performs dependency injection on call of doInjection method.
 * Currently only used from MainActivity
 */
@Component(modules = [ AppModule::class, UtilsModule::class ])
@Singleton
interface AppComponent {
    fun doInjection(mainActivity: MainActivity)
}