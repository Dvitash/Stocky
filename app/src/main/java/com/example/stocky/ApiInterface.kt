package com.example.stocky

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("search")
    suspend fun searchStocks(
        @Query("q") query: String,
        @Query("token") token: String = API_KEY
    ): Response<SearchResponse>

    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String = API_KEY
    ): Response<QuoteResponse>

    companion object {
        private const val BASE_URL = "https://finnhub.io/api/v1/"
        private const val API_KEY = "cvurdcpr01qjg13b8lj0cvurdcpr01qjg13b8ljg"

        fun create(): ApiInterface {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(ApiInterface::class.java)
        }
    }
}
