package com.traderevchallenge.avikd.traderevchallenge.datasource

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import com.traderevchallenge.avikd.traderevchallenge.network.Repository
import io.reactivex.disposables.CompositeDisposable


class PhotosDataSourceFactory(
    private val repository: Repository,
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, PhotosBase>() {

    val mutableLiveData: MutableLiveData<PhotosDataSourceClass>

    init {
        mutableLiveData = MutableLiveData()
    }

    override fun create(): DataSource<Int, PhotosBase> {
        val dataSourceClass = PhotosDataSourceClass(repository, compositeDisposable)
        mutableLiveData.postValue(dataSourceClass)
        return dataSourceClass
    }
}
