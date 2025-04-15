package com.example.stocky.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocky.Stock
import com.example.stocky.StockData
import com.example.stocky.QuoteResponse
import kotlinx.coroutines.launch

data class StockWithQuote(
    val stock: Stock,
    val quote: QuoteResponse?
)

class MainViewModel(private val repository: StockData) : ViewModel() {

    private val _watchlistStocks = MutableLiveData<List<StockWithQuote>>(emptyList())
    val watchlistStocks: LiveData<List<StockWithQuote>> = _watchlistStocks

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private var lastFetchedSymbols: Set<String> = emptySet()

    fun loadWatchlist(force: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true

            val entries = repository.getAllWatchlistEntries()
            val currentSymbols = entries.map { it.symbol }.toSet()

            if (force || currentSymbols != lastFetchedSymbols) {
                lastFetchedSymbols = currentSymbols

                val stocksWithQuotes = currentSymbols.mapNotNull { symbol ->
                    val stock = repository.getStockDetails(symbol)
                    stock?.let {
                        val quote = repository.getQuote(symbol)
                        StockWithQuote(stock = it, quote = quote)
                    }
                }

                _watchlistStocks.value = stocksWithQuotes
            }

            _isLoading.value = false
        }
    }

    fun removeStockFromWatchlist(symbol: String) {
        viewModelScope.launch {
            repository.removeStockFromWatchlist(symbol)
            loadWatchlist()
        }
    }
}
