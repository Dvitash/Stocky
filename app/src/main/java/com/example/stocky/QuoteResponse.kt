package com.example.stocky

data class QuoteResponse(
    val c: Double,
    val d: Double?,
    val dp: Double?,
    val h: Double?,
    val l: Double?,
    val o: Double?,
    val pc: Double?,
    val t: Long?
)