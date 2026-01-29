package ru.fefu.starwarsexplorer.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class FavoritesViewModel : ViewModel() {
    private var _favoriteIds: Set<Int> by mutableStateOf(emptySet())

    val favoriteIds: Set<Int>
        get() = _favoriteIds

    fun addFavorite(id: Int) {
        _favoriteIds = _favoriteIds + id
    }

    fun removeFavorite(id: Int) {
        _favoriteIds = _favoriteIds - id
    }

    fun isFavorite(id: Int): Boolean {
        return id in _favoriteIds
    }

    fun toggleFavorite(id: Int) {
        if (isFavorite(id)) {
            removeFavorite(id)
        } else {
            addFavorite(id)
        }
    }
}