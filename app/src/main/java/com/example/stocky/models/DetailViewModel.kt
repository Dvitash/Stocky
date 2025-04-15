package com.example.stocky.models

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
            _isLoading.value = true
            _stock.value = repository.getStockDetails(symbol)
            _quote.value = repository.getQuote(symbol)
            _isLoading.value = false
        }
    }
}
