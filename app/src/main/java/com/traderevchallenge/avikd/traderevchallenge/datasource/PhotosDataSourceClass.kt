package com.traderevchallenge.avikd.traderevchallenge.datasource

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import com.traderevchallenge.avikd.traderevchallenge.network.ApiResponse
import com.traderevchallenge.avikd.traderevchallenge.network.Repository
import com.traderevchallenge.avikd.traderevchallenge.utils.ApiKeyProvider
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

class PhotosDataSourceClass internal constructor(
    private val repository: Repository,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, PhotosBase>() {
    val progressLiveStatus: MutableLiveData<ApiResponse<Any?>> = MutableLiveData()
    private var completable: Completable? = null
    @SuppressLint("CheckResult")
    override fun loadInitial(
        params: PageKeyedDataSource.LoadInitialParams<Int>,
        callback: PageKeyedDataSource.LoadInitialCallback<Int, PhotosBase>
    ) {
        compositeDisposable.add(repository.executePhotos(
            ApiKeyProvider.fetchApiKey(),
            1
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { disposable -> progressLiveStatus.postValue(ApiResponse.loading()) }
            .subscribe(
                { result ->
                    run {
                        progressLiveStatus.postValue(ApiResponse.success(result))
                        callback.onResult(result, null, 2)
                    }
                },
                { throwable -> progressLiveStatus.postValue(ApiResponse.error(throwable)) }
            ))
    }

    override fun loadBefore(
        params: PageKeyedDataSource.LoadParams<Int>,
        callback: PageKeyedDataSource.LoadCallback<Int, PhotosBase>
    ) {

    }

    @SuppressLint("CheckResult")
    override fun loadAfter(
        params: PageKeyedDataSource.LoadParams<Int>,
        callback: PageKeyedDataSource.LoadCallback<Int, PhotosBase>
    ) {
        compositeDisposable.add(repository.executePhotos(
            ApiKeyProvider.fetchApiKey(),
            params.key
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> progressLiveStatus.postValue(ApiResponse.loading()) }
            .subscribe(
                { result ->
                    progressLiveStatus.postValue(ApiResponse.success(result))
                    callback.onResult(
                        result,
                        params.key + 1
                    )
                },
                { throwable -> progressLiveStatus.postValue(ApiResponse.error(throwable)) }
            ))
    }
}
