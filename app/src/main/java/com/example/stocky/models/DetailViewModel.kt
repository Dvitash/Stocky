package com.example.stocky.models

import android.util.Log
import androidx.lifecycle.*
import com.example.stocky.QuoteResponse
import com.example.stocky.Stock
import com.example.stocky.StockData
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: StockData) : ViewModel() {
    private val _stock = MutableLiveData<Stock?>()
    val stock: LiveData<Stock?> = _stock

    private val _quote = MutableLiveData<QuoteResponse?>()
    val quote: LiveData<QuoteResponse?> = _quote

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadStockDetails(symbol: String) {
        viewModelScope.launch {
            Log.d("DetailViewModel", "Loading details for $symbol...")
            _isLoading.value = true
            try {
                Log.d("DetailViewModel", "Fetching stock details for $symbol")
                _stock.value = repository.getStockDetails(symbol)
                Log.d("DetailViewModel", "Fetching quote for $symbol")
                _quote.value = repository.getQuote(symbol)
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error loading details for $symbol", e)
            }
            _isLoading.value = false
            Log.d("DetailViewModel", "Finished loading details for $symbol")
        }
    }
}
