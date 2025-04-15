package com.example.stocky.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocky.AppDatabase
import com.example.stocky.Stock
import com.example.stocky.StockData
import com.example.stocky.models.MainViewModel
import com.example.stocky.ui.ViewModelFactory
import com.example.stocky.models.StockWithQuote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onStockClick: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    val context = LocalContext.current

    val factory = remember {
        val dao = AppDatabase.getDatabase(context).watchlistDao()
        val repository = StockData(dao)
        ViewModelFactory(repository)
    }

    val viewModel: MainViewModel = viewModel(factory = factory)
    val stocks by viewModel.watchlistStocks.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(true)

    // For delete confirmation dialog
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (pendingDeleteSymbol, setPendingDeleteSymbol) = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadWatchlist()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watchlist", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                actions = {
                    IconButton(onClick = { viewModel.loadWatchlist(force = true) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .padding(8.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
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

                stocks.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No stocks in your watchlist",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onNavigateToSearch) {
                            Text("Add Stocks")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(stocks) { stockWithQuote ->
                            StockCard(
                                stockWithQuote = stockWithQuote,
                                onClick = { onStockClick(stockWithQuote.stock.symbol) },
                                onRemove = {
                                    setPendingDeleteSymbol(stockWithQuote.stock.symbol)
                                    setShowDialog(true)
                                },
                                onShare = {
                                    val stock = stockWithQuote.stock
                                    val quote = stockWithQuote.quote
                                    val shareText = buildString {
                                        append("${stock.symbol} - ${stock.description}")
                                        quote?.let {
                                            append("\nCurrent Price: $${it.c}")
                                            append("\nChange: ${it.d} (${it.dp}%)")
                                        }
                                    }
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Check out this stock: ${stock.symbol}")
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                                }
                            )
                        }
                    }
                }
            }
        }
        if (showDialog && pendingDeleteSymbol != null) {
            AlertDialog(
                onDismissRequest = { setShowDialog(false) },
                title = { Text("Remove Stock") },
                text = { Text("Are you sure you want to remove $pendingDeleteSymbol from your watchlist?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.removeStockFromWatchlist(pendingDeleteSymbol)
                        setShowDialog(false)
                        setPendingDeleteSymbol(null)
                    }) {
                        Text("Remove", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        setShowDialog(false)
                        setPendingDeleteSymbol(null)
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun StockCard(
    stockWithQuote: StockWithQuote,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onShare: () -> Unit
) {
    val stock = stockWithQuote.stock
    val quote = stockWithQuote.quote
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stock.symbol,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stock.type,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                if (stock.description.isNotBlank()) {
                    Text(
                        text = stock.description,
                        fontSize = 15.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                quote?.let {
                    Text(
                        text = "Price: $${it.c}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                    val changeColor = when {
                        it.d?.let { d -> d > 0 } == true -> Color(0xFF4CAF50)
                        it.d?.let { d -> d < 0 } == true -> Color(0xFFF44336)
                        else -> Color.Gray
                    }
                    Text(
                        text = "${if (it.d?.let { d -> d > 0 } == true) "+" else ""}${it.d} (${it.dp}%)",
                        color = changeColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onShare) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = "Share", tint = Color.Black)
            }
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove", tint = Color.Black)
            }
        }
    }
}