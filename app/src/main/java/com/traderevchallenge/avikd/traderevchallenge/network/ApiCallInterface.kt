package com.traderevchallenge.avikd.traderevchallenge.network

import com.traderevchallenge.avikd.traderevchallenge.appconstants.UrlConstants
import com.traderevchallenge.avikd.traderevchallenge.models.PhotoByIdAPIBase
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * ApiCallInterface
 * ApiCallInterface to define API requests for retrofit and okhttp
 */
interface ApiCallInterface {

    @GET(UrlConstants.PHOTOS_URL)
    fun fetchPopularPhotos(@Query("client_id") client_id: String, @Query("page") page: Int, @Query("per_page") per_page: Int): Observable<List<PhotosBase>>
    @GET(UrlConstants.PHOTOS_URL+"/{photoId}")
    fun fetchPhotoDetailsById(@Path(value = "photoId", encoded = true) photoId: String?, @Query("client_id") consumer_key: String): Observable<PhotoByIdAPIBase>
}