package william.miranda.rabobankassignment.ui.main

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import william.miranda.rabobankassignment.domain.model.User
import william.miranda.rabobankassignment.domain.usecase.DownloadFileUseCase
import william.miranda.rabobankassignment.domain.usecase.ParseFileUseCase
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val parseFileUseCase: ParseFileUseCase,
    private val downloadFileUseCase: DownloadFileUseCase
) : ViewModel() {

    private companion object {
        private const val STATE_KEY = "State"
        private const val SESSION_KEY = "Session"
        private const val TARGET_FILE = "file.csv"
        private const val PAGE_SIZE = 50
    }

    private lateinit var parserSession: String

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
        savedStateHandle.get<UiState>(STATE_KEY)?.let { _uiState.tryEmit(it) }
        savedStateHandle.get<String>(SESSION_KEY)?.let { parserSession = it }
    }

    /**
     * Update the State and Persist into the savedStateHandler
     */
    private suspend fun updateState(state: UiState) {
        savedStateHandle[STATE_KEY] = state
        savedStateHandle[SESSION_KEY] = parserSession
        _uiState.emit(state)
    }

    /**
     * Initial method called to start the process
     * Here we call the Downloader and then Parse the initial Items defined on PAGE_SIZE
     */
    fun downloadAndParse(fileUrl: String) {
        viewModelScope.launch {

            try {
                _uiState.emit(UiState.Loading)
                parserSession = UUID.randomUUID().toString()

                downloadFile(csvFile = fileUrl)
                val models = parseUsers(parserSession)

                updateState(UiState.Success(models))
            } catch (ex: Exception) {
                updateState(UiState.Error(ex.message ?: ""))
            }
        }
    }

    /**
     * Method to Request More items from the List
     * Since the file was already downloaded, we need only to request new items from the Parser
     * And, we need to manage all the Items here.
     */
    fun downloadMore() {
        viewModelScope.launch {

            try {
                val currentUsers = uiState.value.data ?: emptyList()
                val moreUsers = parseUsers(parserSession)
                val newList = currentUsers + moreUsers

                updateState(UiState.Success(newList))
            } catch (ex: Exception) {
                updateState(UiState.Error(ex.message ?: ""))
            }

        }
    }

    /**
     * Invoke the UseCase to Download the File
     */
    private suspend fun downloadFile(
        csvFile: String
    ) {
        val targetFile = File(context.cacheDir, TARGET_FILE)

        downloadFileUseCase.run(
            sourceUrl = csvFile,
            targetFile = targetFile
        )
    }

    /**
     * Invoke the UseCase to get more Users
     */
    private suspend fun parseUsers(
        sessionName: String
    ): List<User> {
        val targetFile = File(context.cacheDir, TARGET_FILE)
        return parseFileUseCase.run(
            sessionName = sessionName,
            file = targetFile,
            pageSize = PAGE_SIZE
        )
    }

}