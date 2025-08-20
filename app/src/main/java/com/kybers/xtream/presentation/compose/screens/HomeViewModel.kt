package com.kybers.xtream.presentation.compose.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kybers.xtream.data.entities.VodType
import com.kybers.xtream.data.repository.ChannelRepository
import com.kybers.xtream.data.repository.VodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val channelCount: Int = 0,
    val movieCount: Int = 0,
    val seriesCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val channelRepository: ChannelRepository,
    private val vodRepository: VodRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val channelCount = channelRepository.getChannelCount()
                val movieCount = vodRepository.getVodCountByType(VodType.MOVIE)
                val seriesCount = vodRepository.getVodCountByType(VodType.SERIES)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    channelCount = channelCount,
                    movieCount = movieCount,
                    seriesCount = seriesCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
    
    fun refresh() {
        loadStatistics()
    }
}