package com.traderevchallenge.avikd.traderevchallenge.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.traderevchallenge.avikd.traderevchallenge.appconstants.AppConstants
import com.traderevchallenge.avikd.traderevchallenge.datasource.PhotosDataSourceClass
import com.traderevchallenge.avikd.traderevchallenge.datasource.PhotosDataSourceFactory
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import com.traderevchallenge.avikd.traderevchallenge.network.ApiResponse
import com.traderevchallenge.avikd.traderevchallenge.network.Repository
import com.traderevchallenge.avikd.traderevchallenge.utils.ApiKeyProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FullPhotoDisplayViewModel(repository: Repository) : ViewModel() {

    private val mRepository = repository
    var mPhotoId: String? = ""
    var progressLiveStatus: MutableLiveData<ApiResponse<Any?>> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()
    fun hitPhotosByIdApi(context: Context) {
        compositeDisposable.add(mRepository.executePhotoDetailsById(
            ApiKeyProvider.fetchApiKey(),
            mPhotoId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { disposable -> progressLiveStatus.postValue(ApiResponse.loading()) }
            .subscribe(
                { result ->
                    run {
                        progressLiveStatus.postValue(ApiResponse.success(result))
                    }
                },
                { throwable -> progressLiveStatus.postValue(ApiResponse.error(throwable)) }
            ))
    }

    private val photosDataSourceFactory: PhotosDataSourceFactory
    var firstLoad = true
    var listLiveData: LiveData<PagedList<PhotosBase>>
        private set

    var progressLoadStatus: LiveData<ApiResponse<Any?>> = MutableLiveData()
    private val compositeDisposablePagedList = CompositeDisposable()

    init {
        photosDataSourceFactory = PhotosDataSourceFactory(
            repository,
            compositeDisposablePagedList
        )
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(AppConstants.PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE)
            .setPageSize(AppConstants.PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE).build()
        listLiveData = LivePagedListBuilder(photosDataSourceFactory, pagedListConfig)
            .build()
        progressLoadStatus = Transformations.switchMap(
            photosDataSourceFactory.mutableLiveData, PhotosDataSourceClass::progressLiveStatus
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposablePagedList.clear()
    }
}