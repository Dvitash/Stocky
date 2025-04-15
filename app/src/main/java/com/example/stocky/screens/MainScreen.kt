package com.example.stocky.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onStockClick: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watchlist", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                actions = {
                    IconButton(onClick = { }) {
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    StockCard(
                        onClick = { onStockClick("TSLA") },
                        onRemove = { },
                        onShare = { }
                    )
                }
            }
        }
    }
}

@Composable
fun StockCard(
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onShare: () -> Unit
) {
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
                        text = "TSLA",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Common Stock",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "Tesla",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Price: $${245.67}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "(+${0.5}%)",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
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