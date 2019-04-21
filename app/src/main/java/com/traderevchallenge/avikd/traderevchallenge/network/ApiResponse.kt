package com.traderevchallenge.avikd.traderevchallenge.network

import androidx.annotation.NonNull
import org.jetbrains.annotations.Nullable


class ApiResponse<T> private constructor(
    val status: Status?, @param:Nullable @field:Nullable
    val data: T?, @param:Nullable @field:Nullable
    val error: Throwable?
) {
    companion object {

        fun loading(): ApiResponse<Any?> {
            return ApiResponse(Status.LOADING, null, null)
        }

        fun <T> success(@NonNull data: T): ApiResponse<Any?> {
            return ApiResponse(Status.SUCCESS, data, null)
        }

        fun error(@NonNull error: Throwable): ApiResponse<Any?> {
            return ApiResponse(Status.ERROR, null, error)
        }
    }

}