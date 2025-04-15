import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocky.AppDatabase
import com.example.stocky.Stock
import com.example.stocky.StockData
import com.example.stocky.models.SearchViewModel
import com.example.stocky.ui.ViewModelFactory

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current

    val factory = remember {
        val dao = AppDatabase.getDatabase(context).watchlistDao()
        val repository = StockData(dao)
        ViewModelFactory(repository)
    }

    val viewModel: SearchViewModel = viewModel(factory = factory)
    val watchlistSymbols by viewModel.watchlistSymbols.observeAsState(setOf())

    var query by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val searchResults by viewModel.searchResults.observeAsState(emptyList())

    // Stop loading when results update
    LaunchedEffect(searchResults) {
        if (loading) loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { onNavigateBack() }) {
            Text(text = "Back to Main")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search for a stock") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    loading = true
                    viewModel.searchStocks(query)
                }
            ) {
                Text(text = "Search")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults ?: emptyList()) { stock ->
                    val isInWatchlist = watchlistSymbols.contains(stock.symbol)
                    StockListItem(
                        stock = stock,
                        isInWatchlist = isInWatchlist,
                        onAdd = { viewModel.addStockToWatchlist(stock.symbol) },
                        onRemove = { viewModel.removeStockFromWatchlist(stock.symbol) }
                    )
                }
            }
        }
    }
}

@Composable
fun StockListItem(
    stock: Stock,
    isInWatchlist: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stock.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stock.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { if (isInWatchlist) onRemove() else onAdd() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isInWatchlist) "✕" else "+",
                    color = if (isInWatchlist) Color.Gray else Color(0xFF4CAF50), // Green for +
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}