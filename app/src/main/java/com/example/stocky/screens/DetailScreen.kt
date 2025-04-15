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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocky.models.DetailViewModel
import com.example.stocky.ui.ViewModelFactory
import android.content.Intent
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
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

    fun refresh() {
        viewModel.loadStockDetails(stockSymbol)
    }

    fun shareStock() {
        val shareText = buildString {
            append("${stock?.symbol ?: ""} - ${stock?.description ?: ""}")
            quote?.let {
                append("\nCurrent Price: $${it.c}")
                append("\nChange: ${it.d} (${it.dp}%)")
            }
        }
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this stock: ${stock?.symbol ?: ""}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stock?.symbol ?: "Stock Detail",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { refresh() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { shareStock() }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                stock == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Stock data not available", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = stock?.symbol ?: "",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (!stock?.description.isNullOrBlank()) {
                                Text(
                                    text = stock?.description ?: "",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                                )
                            }
                            Text(
                                text = stock?.type ?: "",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(top = 2.dp, bottom = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            quote?.let {
                                Text(
                                    text = "Current Price: $${it.c}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                val changeColor = when {
                                    it.d?.let { d -> d > 0 } == true -> Color(0xFF4CAF50)
                                    it.d?.let { d -> d < 0 } == true -> Color(0xFFF44336)
                                    else -> MaterialTheme.colorScheme.onBackground
                                }
                                Text(
                                    text = "${if (it.d?.let { d -> d > 0 } == true) "+" else ""}${it.d} (${it.dp}%)",
                                    color = changeColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Open: $${it.o}")
                                Text("High: $${it.h}")
                                Text("Low: $${it.l}")
                                Text("Prev Close: $${it.pc}")
                            } ?: Text("Quote data not available", color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }
        }
    }
}