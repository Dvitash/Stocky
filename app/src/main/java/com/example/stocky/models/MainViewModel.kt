package com.example.stocky.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocky.Stock
import com.example.stocky.StockData
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StockData) : ViewModel() {

    private val _watchlistStocks = MutableLiveData<List<Stock>>(emptyList())
    val watchlistStocks: LiveData<List<Stock>> = _watchlistStocks

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private var lastFetchedSymbols: Set<String> = emptySet()

    fun loadWatchlist() {
        viewModelScope.launch {
            _isLoading.value = true

            val entries = repository.getAllWatchlistEntries()
            val currentSymbols = entries.map { it.symbol }.toSet()

            if (currentSymbols != lastFetchedSymbols) {
                lastFetchedSymbols = currentSymbols

                val stocks = currentSymbols.mapNotNull { symbol ->
                    repository.getStockDetails(symbol)
                }

                _watchlistStocks.value = stocks
            }

            _isLoading.value = false
        }
    }
}
