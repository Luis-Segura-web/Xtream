package com.kybers.xtream.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kybers.xtream.data.model.Movie
import com.kybers.xtream.ui.common.SortOption
import com.kybers.xtream.ui.common.SortSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MoviesUiState(
    val isLoading: Boolean = false,
    val categories: Map<String, List<Movie>> = emptyMap(),
    val totalMovies: Int = 0,
    val totalCategories: Int = 0,
    val searchQuery: String = "",
    val sortSettings: SortSettings = SortSettings(),
    val error: String? = null
)

class MoviesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    private var allMovies: List<Movie> = emptyList()

    fun loadMovies(movies: List<Movie>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                allMovies = movies
                processMoviesAsync()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error cargando películas: ${e.message}"
                )
            }
        }
    }

    fun searchMovies(query: String) {
        if (_uiState.value.searchQuery == query) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                searchQuery = query
            )
            processMoviesAsync()
        }
    }

    fun applySorting(sortSettings: SortSettings) {
        if (_uiState.value.sortSettings == sortSettings) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                sortSettings = sortSettings
            )
            processMoviesAsync()
        }
    }

    private suspend fun processMoviesAsync() {
        withContext(Dispatchers.Default) {
            try {
                val currentState = _uiState.value
                
                // Filtrar por búsqueda
                val filteredMovies = if (currentState.searchQuery.isEmpty()) {
                    allMovies
                } else {
                    allMovies.filter { movie ->
                        movie.name.contains(currentState.searchQuery, ignoreCase = true)
                    }
                }

                // Aplicar ordenamiento
                val sortedMovies = applySortToMovies(filteredMovies, currentState.sortSettings)
                
                // Agrupar por categorías
                val categories = sortedMovies.groupBy { it.category }

                // Actualizar UI en el hilo principal
                withContext(Dispatchers.Main) {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        categories = categories,
                        totalMovies = sortedMovies.size,
                        totalCategories = categories.size,
                        error = null
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error procesando películas: ${e.message}"
                    )
                }
            }
        }
    }

    private fun applySortToMovies(
        movies: List<Movie>,
        settings: SortSettings
    ): List<Movie> {
        val groupedMovies = movies.groupBy { it.category }
        
        // Sort categories
        val sortedCategories = when (settings.categoriesSort) {
            SortOption.A_Z -> groupedMovies.toSortedMap()
            SortOption.Z_A -> groupedMovies.toSortedMap(reverseOrder())
            SortOption.DEFAULT -> groupedMovies
        }
        
        // Sort movies within each category and flatten
        return sortedCategories.flatMap { (_, categoryMovies) ->
            when (settings.itemsSort) {
                SortOption.A_Z -> categoryMovies.sortedBy { it.name.lowercase() }
                SortOption.Z_A -> categoryMovies.sortedByDescending { it.name.lowercase() }
                SortOption.DEFAULT -> categoryMovies
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}