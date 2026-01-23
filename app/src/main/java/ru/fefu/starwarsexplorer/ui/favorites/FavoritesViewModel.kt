package ru.fefu.starwarsexplorer.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.fefu.starwarsexplorer.data.model.Person
import ru.fefu.starwarsexplorer.data.repository.SwapiRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf

sealed class FavoritesUiState {
    object Loading : FavoritesUiState()
    data class Success(val favorites: List<Person>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}

class FavoritesViewModel(private val repository: SwapiRepository) : ViewModel() {

    private val _favoriteIds = mutableStateOf<Set<Int>>(emptySet())
    var favoriteIds: Set<Int> by _favoriteIds
        private set

    var uiState by mutableStateOf<FavoritesUiState>(FavoritesUiState.Loading)
        private set

    fun addFavorite(id: Int) {
        _favoriteIds.value = _favoriteIds.value + id
        loadFavorites()
    }

    fun removeFavorite(id: Int) {
        _favoriteIds.value = _favoriteIds.value - id
        loadFavorites()
    }

    fun isFavorite(id: Int): Boolean {
        return favoriteIds.contains(id)
    }

    fun loadFavorites() {
        if (favoriteIds.isEmpty()) {
            uiState = FavoritesUiState.Success(emptyList())
            return
        }

        uiState = FavoritesUiState.Loading
        viewModelScope.launch {
            try {
                val favorites = mutableListOf<Person>()
                favoriteIds.forEach { id ->
                    try {
                        val person = repository.getPerson(id)
                        favorites.add(person)
                    } catch (e: Exception) {
                    }
                }
                uiState = FavoritesUiState.Success(favorites)
            } catch (e: Exception) {
                uiState = FavoritesUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun toggleFavorite(id: Int) {
        if (isFavorite(id)) {
            removeFavorite(id)
        } else {
            addFavorite(id)
        }
    }
}