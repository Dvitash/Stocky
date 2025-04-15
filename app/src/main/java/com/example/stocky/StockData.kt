package com.example.stocky

import com.example.stocky.ApiInterface
import com.example.stocky.WatchlistDao
import com.example.stocky.Stock
import com.example.stocky.WatchlistEntry
import android.util.Log
import retrofit2.Response

class StockData(private val watchlistDao: WatchlistDao) {
    private val api = ApiInterface.create()

    suspend fun searchStocks(query: String): List<Stock>? {
        val response = api.searchStocks(query)
        Log.d("StockData", "Search Response: ${response.code()} ${response.message()} - Body: ${response.body()}")
        return if (response.isSuccessful) {
            response.body()?.result
        } else {
            Log.e("StockData", "Search failed: ${response.errorBody()?.string()}")
            null
        }
    }

    suspend fun getStockDetails(symbol: String): Stock? {
        return try {
            val response = api.searchStocks(symbol)

            if (!response.isSuccessful) {
                Log.e("StockData", "API search failed for $symbol: ${response.errorBody()?.string()}")
                return null
            }

            val results = response.body()?.result
            val matchedStock = results?.firstOrNull {
                it.symbol.equals(symbol, ignoreCase = true)
            }

            Log.d("StockData", "Stock match for $symbol: $matchedStock")

            matchedStock
        } catch (e: Exception) {
            Log.e("StockData", "Exception during getStockDetails($symbol)", e)
            null
        }
    }

    suspend fun getQuote(symbol: String): QuoteResponse? {
        return try {
            val response = api.getQuote(symbol)

            if (response.isSuccessful) {
                val quote = response.body()
                Log.d("StockData", "Quote for $symbol: $quote")
                quote
            } else {
                Log.e("StockData", "Quote failed for $symbol: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("StockData", "Exception during getQuote($symbol)", e)
            null
        }
    }

    // watchlist operations
    suspend fun addStockToWatchlist(symbol: String) {
        val entry = WatchlistEntry(symbol = symbol, addedAt = System.currentTimeMillis())
        watchlistDao.insertEntry(entry)
    }

    suspend fun removeStockFromWatchlist(symbol: String) {
        watchlistDao.deleteBySymbol(symbol)
    }

    suspend fun getAllWatchlistEntries(): List<WatchlistEntry> {
        val entries = watchlistDao.getAllEntries()
        return entries
    }

}
