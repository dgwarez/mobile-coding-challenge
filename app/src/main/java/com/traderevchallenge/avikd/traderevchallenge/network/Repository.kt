package com.traderevchallenge.avikd.traderevchallenge.network

import com.traderevchallenge.avikd.traderevchallenge.appconstants.AppConstants.PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE
import com.traderevchallenge.avikd.traderevchallenge.models.PhotoByIdAPIBase
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import io.reactivex.Observable


class Repository(private val apiCallInterface: ApiCallInterface) {

    /*
     * method to call list of photos api
     * */
    fun executePhotos(client_id: String, pageIndex : Int): Observable<List<PhotosBase>> {
        return apiCallInterface.fetchPopularPhotos(client_id, pageIndex, PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE)
    }

    /*
     * method to call fetch Photo details with ID api
     * */
    fun executePhotoDetailsById(client_id: String, photoId: String?): Observable<PhotoByIdAPIBase> {
        return apiCallInterface.fetchPhotoDetailsById(photoId, client_id)
    }

}