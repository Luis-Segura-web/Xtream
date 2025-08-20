package com.kybers.xtream.presentation.compose.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kybers.xtream.data.entities.Channel
import com.kybers.xtream.data.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChannelsUiState(
    val searchQuery: String = "",
    val selectedGroup: String? = null,
    val selectedCountry: String? = null,
    val selectedLanguage: String? = null,
    val showFavoritesOnly: Boolean = false
)

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val channelRepository: ChannelRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChannelsUiState())
    val uiState: StateFlow<ChannelsUiState> = _uiState.asStateFlow()
    
    val channels: Flow<PagingData<Channel>> = uiState
        .map { state ->
            when {
                state.searchQuery.isNotEmpty() -> {
                    channelRepository.searchChannelsPaged(state.searchQuery)
                }
                state.showFavoritesOnly -> {
                    channelRepository.getFavoriteChannelsPaged()
                }
                state.selectedGroup != null -> {
                    channelRepository.getChannelsByGroupPaged(state.selectedGroup)
                }
                state.selectedCountry != null -> {
                    channelRepository.getChannelsByCountryPaged(state.selectedCountry)
                }
                state.selectedLanguage != null -> {
                    channelRepository.getChannelsByLanguagePaged(state.selectedLanguage)
                }
                else -> {
                    channelRepository.getAllChannelsPaged()
                }
            }
        }
        .flatMapLatest { it }
        .cachedIn(viewModelScope)
    
    val groups: Flow<List<String>> = channelRepository.getChannelGroups()
    val countries: Flow<List<String>> = channelRepository.getChannelCountries()
    val languages: Flow<List<String>> = channelRepository.getChannelLanguages()
    
    fun search(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
    
    fun filterByGroup(group: String?) {
        _uiState.value = _uiState.value.copy(
            selectedGroup = group,
            selectedCountry = null,
            selectedLanguage = null,
            showFavoritesOnly = false,
            searchQuery = ""
        )
    }
    
    fun filterByCountry(country: String?) {
        _uiState.value = _uiState.value.copy(
            selectedCountry = country,
            selectedGroup = null,
            selectedLanguage = null,
            showFavoritesOnly = false,
            searchQuery = ""
        )
    }
    
    fun filterByLanguage(language: String?) {
        _uiState.value = _uiState.value.copy(
            selectedLanguage = language,
            selectedGroup = null,
            selectedCountry = null,
            showFavoritesOnly = false,
            searchQuery = ""
        )
    }
    
    fun showFavoritesOnly(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showFavoritesOnly = show,
            selectedGroup = null,
            selectedCountry = null,
            selectedLanguage = null,
            searchQuery = ""
        )
    }
    
    fun clearFilters() {
        _uiState.value = ChannelsUiState()
    }
    
    fun playChannel(channel: Channel) {
        viewModelScope.launch {
            channelRepository.updateLastSeen(channel.id)
            // TODO: Launch player activity
        }
    }
    
    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            channelRepository.updateFavoriteStatus(channel.id, !channel.isFavorite)
        }
    }
}