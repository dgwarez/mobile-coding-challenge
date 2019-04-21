package com.traderevchallenge.avikd.traderevchallenge.modules

import com.traderevchallenge.avikd.traderevchallenge.MainActivity
import dagger.Component
import javax.inject.Singleton


@Component(modules = [ AppModule::class, UtilsModule::class ])
@Singleton
interface AppComponent {
    fun doInjection(mainActivity: MainActivity)
    //fun doInjectionPhotos(fullPhotoDisplayActivity: FullPhotoDisplayActivity)
}