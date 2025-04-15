package com.example.stocky

data class SearchResponse(
    val count: Int,
    val result: List<Stock>
)