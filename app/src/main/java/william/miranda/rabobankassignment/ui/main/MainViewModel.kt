package william.miranda.rabobankassignment.ui.main

import androidx.lifecycle.SavedStateHandle
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
    private val savedStateHandle: SavedStateHandle,
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private companion object {
        private const val STATE_KEY = "State"
    }

    /**
     * Flow to be observed
     */
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    init {
        /**
         * Emit the Saved State if we have it
         * Note that we have a limit for Parcelable size to go into the Bundle so,
         * if the Record number is too high, this may lead to issues
         */
        savedStateHandle.get<UiState>(STATE_KEY)?.let {
            _uiState.tryEmit(it)
        }
    }

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
                UiState.Success(model).also {
                    savedStateHandle[STATE_KEY] = it
                    _uiState.emit(it)
                }
            } catch (ex: Exception) {
                _uiState.emit(UiState.Error(ex.message ?: ""))
            }

        }
    }

}