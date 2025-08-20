package com.kybers.xtream.presentation.compose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kybers.xtream.presentation.compose.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsScreen(
    viewModel: ChannelsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val channels = viewModel.channels.collectAsLazyPagingItems()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Canales de TV",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Search Bar
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = viewModel::search,
            placeholder = "Buscar canales...",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Content
        when (channels.loadState.refresh) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator("Cargando canales...")
                }
            }
            
            is LoadState.Error -> {
                val error = channels.loadState.refresh as LoadState.Error
                ErrorMessage(
                    message = error.error.message ?: "Error al cargar canales",
                    onRetry = { channels.retry() }
                )
            }
            
            else -> {
                if (channels.itemCount == 0) {
                    EmptyState("No se encontraron canales")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            count = channels.itemCount,
                            key = channels.itemKey { it.id }
                        ) { index ->
                            val channel = channels[index]
                            if (channel != null) {
                                ChannelCard(
                                    channel = channel,
                                    onClick = { viewModel.playChannel(it) },
                                    onFavoriteClick = { viewModel.toggleFavorite(it) }
                                )
                            }
                        }
                        
                        // Loading indicator for pagination
                        if (channels.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}