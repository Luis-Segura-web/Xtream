package com.kybers.xtream.presentation.compose.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Buscar...",
    modifier: Modifier = Modifier,
    debounceMs: Long = 300L
) {
    var localQuery by remember { mutableStateOf(query) }
    
    // Debounce search
    LaunchedEffect(localQuery) {
        delay(debounceMs)
        if (localQuery != query) {
            onQueryChange(localQuery)
        }
    }
    
    // Update local query when external query changes
    LaunchedEffect(query) {
        if (query != localQuery) {
            localQuery = query
        }
    }
    
    OutlinedTextField(
        value = localQuery,
        onValueChange = { localQuery = it },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = if (localQuery.isNotEmpty()) {
            {
                IconButton(
                    onClick = { 
                        localQuery = ""
                        onQueryChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda"
                    )
                }
            }
        } else null,
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}