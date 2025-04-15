package com.example.stocky

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocky.models.DetailViewModel
import com.example.stocky.ui.ViewModelFactory

@Composable
fun DetailScreen(
    stockSymbol: String,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current

    val factory = remember {
        val dao = AppDatabase.getDatabase(context).watchlistDao()
        val repository = StockData(dao)
        ViewModelFactory(repository)
    }

    val viewModel: DetailViewModel = viewModel(factory = factory)

    val stock by viewModel.stock.observeAsState()
    val quote by viewModel.quote.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)

    LaunchedEffect(stockSymbol) {
        viewModel.loadStockDetails(stockSymbol)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { onNavigateBack() }) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            stock?.let { stockData ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stockData.symbol,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stockData.description,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        quote?.let { quoteData ->
                            Text("Current Price: $${quoteData.c}")
                            Text("Open: $${quoteData.o}")
                            Text("High: $${quoteData.h}")
                            Text("Low: $${quoteData.l}")
                            Text("Prev Close: $${quoteData.pc}")
                            Text("Change: ${quoteData.d} (${quoteData.dp}%)")
                        } ?: Text("Quote data not available")
                    }
                }
            } ?: Text("Stock data not available")
        }
    }
}