package william.miranda.rabobankassignment.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import william.miranda.rabobankassignment.domain.usecase.GetUsersUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    /**
     * Flow to be observed
     */
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    /**
     * Retrieve the Users and update the UiState
     */
    fun fetchUsers() {

        viewModelScope.launch {
            val csvFile =
                "https://raw.githubusercontent.com/RabobankDev/AssignmentCSV/main/issues.csv"

            _uiState.emit(UiState.Loading)

            try {
                val model = getUsersUseCase.get(csvFile)
                _uiState.emit(UiState.Success(model))
            } catch (ex: Exception) {
                _uiState.emit(UiState.Error(ex.message ?: ""))
            }

        }
    }

}