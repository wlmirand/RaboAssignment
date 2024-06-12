package william.miranda.rabobankassignment.ui.main

import william.miranda.rabobankassignment.domain.model.User

/**
 * Class to Hold the UiState along with needed data
 */
sealed class UiState(
    open val data: List<User>? = null,
    open val error: String? = null
) {
    data object Idle : UiState()
    data object Loading : UiState()

    data class Success(
        override val data: List<User>
    ) : UiState(
        data = data
    )

    data class Error(
        override val error: String
    ) : UiState(
        error = error
    )
}