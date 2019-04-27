package com.traderevchallenge.avikd.traderevchallenge.viewmodels

import javax.inject.Inject
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.traderevchallenge.avikd.traderevchallenge.network.Repository

/**
 * ViewModelFactory
 * ViewModelFactory class to create viewModel objects
 * Instance of repository class is injected using Dagger2 DI
 */
class ViewModelFactory @Inject
constructor(private val repository: Repository) : ViewModelProvider.Factory {


    @NonNull
    override fun <T : ViewModel> create(@NonNull modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotosViewModel::class.java)) {
            return PhotosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}