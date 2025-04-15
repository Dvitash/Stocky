package com.example.stocky.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stocky.StockData
import com.example.stocky.models.DetailViewModel
import com.example.stocky.models.MainViewModel
import com.example.stocky.models.SearchViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: StockData) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(repository) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) ->
                SearchViewModel(repository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) ->
                DetailViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
