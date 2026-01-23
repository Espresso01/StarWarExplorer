package ru.fefu.starwarsexplorer.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.fefu.starwarsexplorer.data.model.Person
import ru.fefu.starwarsexplorer.data.repository.SwapiRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class PersonDetailUiState {
    object Loading : PersonDetailUiState()
    data class Success(val person: Person) : PersonDetailUiState()
    data class Error(val message: String) : PersonDetailUiState()
}

class PersonDetailViewModel(private val repository: SwapiRepository) : ViewModel() {

    var uiState by mutableStateOf<PersonDetailUiState>(PersonDetailUiState.Loading)
        private set

    fun loadPerson(id: Int) {
        uiState = PersonDetailUiState.Loading
        viewModelScope.launch {
            try {
                val person = repository.getPerson(id)
                uiState = PersonDetailUiState.Success(person)
            } catch (e: Exception) {
                uiState = PersonDetailUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}