package com.traderevchallenge.avikd.traderevchallenge.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import com.traderevchallenge.avikd.traderevchallenge.network.Repository
import io.reactivex.disposables.CompositeDisposable

/**
 * PhotosDataSourceFactory
 * Data source factory class that creates PhotosDataSourceClass for Android pagination library
 * Also initializes MutableLiveData object used to emit observable data from pagination library
 */
class PhotosDataSourceFactory(
    private val repository: Repository,
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, PhotosBase>() {

    val mutableLiveData: MutableLiveData<PhotosDataSourceClass> = MutableLiveData()

    override fun create(): DataSource<Int, PhotosBase> {
        val dataSourceClass = PhotosDataSourceClass(repository, compositeDisposable)
        mutableLiveData.postValue(dataSourceClass)
        return dataSourceClass
    }
}
