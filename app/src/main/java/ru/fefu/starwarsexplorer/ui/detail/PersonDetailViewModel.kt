package ru.fefu.starwarsexplorer.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.fefu.starwarsexplorer.data.model.Person
import ru.fefu.starwarsexplorer.data.repository.SwapiRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

sealed class PersonDetailUiState {
    object Loading : PersonDetailUiState()
    data class Success(val person: Person) : PersonDetailUiState()
    data class Error(val message: String) : PersonDetailUiState()
}

class PersonDetailViewModel(private val repository: SwapiRepository) : ViewModel() {

    private val _uiState = MutableLiveData<PersonDetailUiState>(PersonDetailUiState.Loading)
    val uiState: LiveData<PersonDetailUiState>
        get() = _uiState

    fun loadPerson(id: Int) {
        _uiState.value = PersonDetailUiState.Loading
        viewModelScope.launch {
            try {
                val person = repository.getPerson(id)
                _uiState.value = PersonDetailUiState.Success(person)
            } catch (e: Exception) {
                _uiState.value = PersonDetailUiState.Error(
                    e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }
}