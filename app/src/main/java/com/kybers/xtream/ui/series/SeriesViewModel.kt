package com.kybers.xtream.ui.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kybers.xtream.data.model.Series
import com.kybers.xtream.ui.common.SortOption
import com.kybers.xtream.ui.common.SortSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SeriesUiState(
    val isLoading: Boolean = false,
    val categories: Map<String, List<Series>> = emptyMap(),
    val totalSeries: Int = 0,
    val totalCategories: Int = 0,
    val searchQuery: String = "",
    val sortSettings: SortSettings = SortSettings(),
    val error: String? = null
)

class SeriesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SeriesUiState())
    val uiState: StateFlow<SeriesUiState> = _uiState.asStateFlow()

    private var allSeries: List<Series> = emptyList()

    fun loadSeries(series: List<Series>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                allSeries = series
                processSeriesAsync()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error cargando series: ${e.message}"
                )
            }
        }
    }

    fun searchSeries(query: String) {
        if (_uiState.value.searchQuery == query) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                searchQuery = query
            )
            processSeriesAsync()
        }
    }

    fun applySorting(sortSettings: SortSettings) {
        if (_uiState.value.sortSettings == sortSettings) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                sortSettings = sortSettings
            )
            processSeriesAsync()
        }
    }

    private suspend fun processSeriesAsync() {
        withContext(Dispatchers.Default) {
            try {
                val currentState = _uiState.value
                
                // Filtrar por búsqueda
                val filteredSeries = if (currentState.searchQuery.isEmpty()) {
                    allSeries
                } else {
                    allSeries.filter { series ->
                        series.name.contains(currentState.searchQuery, ignoreCase = true)
                    }
                }

                // Aplicar ordenamiento
                val sortedSeries = applySortToSeries(filteredSeries, currentState.sortSettings)
                
                // Agrupar por categorías
                val categories = sortedSeries.groupBy { it.category }

                // Actualizar UI en el hilo principal
                withContext(Dispatchers.Main) {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        categories = categories,
                        totalSeries = sortedSeries.size,
                        totalCategories = categories.size,
                        error = null
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error procesando series: ${e.message}"
                    )
                }
            }
        }
    }

    private fun applySortToSeries(
        series: List<Series>,
        settings: SortSettings
    ): List<Series> {
        val groupedSeries = series.groupBy { it.category }
        
        // Sort categories
        val sortedCategories = when (settings.categoriesSort) {
            SortOption.A_Z -> groupedSeries.toSortedMap()
            SortOption.Z_A -> groupedSeries.toSortedMap(reverseOrder())
            SortOption.DEFAULT -> groupedSeries
        }
        
        // Sort series within each category and flatten
        return sortedCategories.flatMap { (_, categorySeries) ->
            when (settings.itemsSort) {
                SortOption.A_Z -> categorySeries.sortedBy { it.name.lowercase() }
                SortOption.Z_A -> categorySeries.sortedByDescending { it.name.lowercase() }
                SortOption.DEFAULT -> categorySeries
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}