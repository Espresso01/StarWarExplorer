package ru.fefu.starwarsexplorer.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.fefu.starwarsexplorer.data.model.Person
import ru.fefu.starwarsexplorer.data.repository.SwapiRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class PeopleUiState {
    object Loading : PeopleUiState()
    data class Success(val people: List<Person>) : PeopleUiState()
    data class Error(val message: String) : PeopleUiState()
}

class PeopleListViewModel(private val repository: SwapiRepository) : ViewModel() {

    var uiState by mutableStateOf<PeopleUiState>(PeopleUiState.Loading)
        private set

    fun loadPeople(search: String? = null) {
        uiState = PeopleUiState.Loading
        viewModelScope.launch {
            try {
                val people = repository.getPeople(search)
                uiState = PeopleUiState.Success(people)
            } catch (e: Exception) {
                uiState = PeopleUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}