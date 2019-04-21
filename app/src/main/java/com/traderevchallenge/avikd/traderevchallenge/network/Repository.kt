package com.traderevchallenge.avikd.traderevchallenge.network

import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import io.reactivex.Observable


class Repository(private val apiCallInterface: ApiCallInterface) {

    /*
     * method to call fetchPopularPhotos api
     * */
    fun executePhotos(client_id: String, pageIndex : Int): Observable<List<PhotosBase>> {
        return apiCallInterface.fetchPopularPhotos(client_id, pageIndex)
    }

    /*
     * method to call fetch Photo details with ID api
     * */
/*    fun executePhotoDetailsById(consumer_key: String, photoId: Int?): Observable<PhotoByIdAPIBase> {
        return apiCallInterface.fetchPhotoDetailsById(photoId.toString(), consumer_key)
    }*/

}