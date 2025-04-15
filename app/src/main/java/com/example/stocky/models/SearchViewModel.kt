package com.example.stocky.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocky.Stock
import com.example.stocky.StockData
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: StockData) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Stock>?>()
    val searchResults: MutableLiveData<List<Stock>?> = _searchResults

    private val _watchlistSymbols = MutableLiveData<Set<String>>(setOf())
    val watchlistSymbols: LiveData<Set<String>> = _watchlistSymbols

    init {
        loadWatchlist()
    }

    fun searchStocks(query: String) {
        viewModelScope.launch {
            val results = repository.searchStocks(query) // Assuming you have this method
            _searchResults.value = results
        }
    }

    fun addStockToWatchlist(symbol: String) {
        viewModelScope.launch {
            repository.addStockToWatchlist(symbol)
            loadWatchlist()
        }
    }

    fun removeStockFromWatchlist(symbol: String) {
        viewModelScope.launch {
            repository.removeStockFromWatchlist(symbol)
            loadWatchlist()
        }
    }

    private fun loadWatchlist() {
        viewModelScope.launch {
            val entries = repository.getAllWatchlistEntries()
            _watchlistSymbols.postValue(entries.map { it.symbol }.toSet())
        }
    }
}
