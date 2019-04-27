package com.traderevchallenge.avikd.traderevchallenge.modules
import android.content.Context
import dagger.Module
import javax.inject.Singleton
import dagger.Provides

/**
 * AppModule
 * AppModule for dependency injection of singleton context application object used throughout the app
 * (Not being used currently - file to be removed)
 */
@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    internal fun provideContext(): Context {
        return context
    }
}