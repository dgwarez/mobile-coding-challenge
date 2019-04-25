package com.traderevchallenge.avikd.traderevchallenge.datasource

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.traderevchallenge.avikd.traderevchallenge.MyApplication
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
        compositeDisposable.clear()
        Log.d("Page No: load Initial= ",MyApplication.currentPageNumber.toString())
        compositeDisposable.add(repository.executePhotos(
            ApiKeyProvider.fetchApiKey(),
            MyApplication.currentPageNumber
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { disposable -> progressLiveStatus.postValue(ApiResponse.loading()) }
            .subscribe(
                { result ->
                    run {
                        progressLiveStatus.postValue(ApiResponse.success(result))
                        var previousPageKey:Int? = null
                        if (MyApplication.currentPageNumber > 1) {
                            previousPageKey = MyApplication.currentPageNumber - 1
                        }
                        callback.onResult(result, previousPageKey, MyApplication.currentPageNumber + 1)
                    }
                },
                { throwable -> progressLiveStatus.postValue(ApiResponse.error(throwable)) }
            ))
    }

    @SuppressLint("CheckResult")
    override fun loadBefore(
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
                    var adjacentParamsKey:Int? = null
                    if (params.key > 1) {
                        adjacentParamsKey = params.key - 1
                    }
                    callback.onResult(
                        result,
                        adjacentParamsKey
                    )
                    MyApplication.currentPageNumber = params.key
                    Log.d("Page No: load Before = ",MyApplication.currentPageNumber.toString())
                },
                { throwable -> progressLiveStatus.postValue(ApiResponse.error(throwable)) }
            ))
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
                    MyApplication.currentPageNumber = params.key
                    Log.d("Page No: load After = ",MyApplication.currentPageNumber.toString())
                },
                { throwable -> progressLiveStatus.postValue(ApiResponse.error(throwable)) }
            ))
    }
}
