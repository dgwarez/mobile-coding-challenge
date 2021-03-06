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

/**
 * PhotosViewModel
 * PhotosViewModel class to for MainActivity
 * Creates live data objects for passing observable data to MainActivity
 */
class PhotosViewModel(repository: Repository) : ViewModel() {

    private val photosDataSourceFactory: PhotosDataSourceFactory
    private val mRepository = repository
    var progressLiveStatus: MutableLiveData<ApiResponse<Any?>> = MutableLiveData()

    var firstLoad = true
    var listLiveData: LiveData<PagedList<PhotosBase>>
        private set

    var progressLoadStatus: LiveData<ApiResponse<Any?>> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()

    init {
        photosDataSourceFactory = PhotosDataSourceFactory(
            repository,
            compositeDisposable
        )
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(AppConstants.PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE)
            .setPageSize(AppConstants.PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE).build()
        listLiveData = LivePagedListBuilder(photosDataSourceFactory, pagedListConfig)
            .build()
        progressLoadStatus = Transformations.switchMap(
            photosDataSourceFactory.mutableLiveData, PhotosDataSourceClass::progressLiveStatus)
    }

    fun hitPhotosByIdApi(context: Context, mPhotoId: String?) {
        compositeDisposable.add(mRepository.executePhotoDetailsById(
            ApiKeyProvider.fetchApiKey(null),
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}