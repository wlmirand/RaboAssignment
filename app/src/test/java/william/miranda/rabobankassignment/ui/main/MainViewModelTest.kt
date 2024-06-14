package william.miranda.rabobankassignment.ui.main

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import william.miranda.rabobankassignment.domain.model.User
import william.miranda.rabobankassignment.domain.usecase.DownloadFileUseCase
import william.miranda.rabobankassignment.domain.usecase.ParseFileUseCase
import java.io.File

class MainViewModelTest {

    lateinit var underTest: MainViewModel

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    @MockK
    private lateinit var parseFileUseCase: ParseFileUseCase

    @MockK
    private lateinit var downloadFileUseCase: DownloadFileUseCase

    @MockK
    private lateinit var cacheFile: File

    @JvmField
    @Rule
    val tempFolder = TemporaryFolder()

    private val dispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        MockKAnnotations.init(this, relaxUnitFun = true)

        every { context.cacheDir } returns tempFolder.root
    }

    @Test
    fun `when saved state handle contains data then restore it on init`() {

        val restoredUiState = UiState.Success(emptyList())
        stubSavedStateHandler(
            uiState = restoredUiState,
            session = "A Session"
        )

        underTest = MainViewModel(
            context = context,
            savedStateHandle = savedStateHandle,
            parseFileUseCase = parseFileUseCase,
            downloadFileUseCase = downloadFileUseCase
        )

        runTest {
            val state = underTest.uiState.first()
            assertEquals(state, restoredUiState)
        }
    }

    @Test
    fun `when request a new download and parser returns valid data then ui state is success`() {
        val expectedList = listOf(
            User(
                firstName = "William",
                surname = "Miranda",
                issueCount = 42,
                dateOfBirth = "23/03/1985",
                avatar = "http://avatar.url"
            )
        )

        stubSavedStateHandler(
            uiState = null,
            session = null
        )

        underTest = MainViewModel(
            context = context,
            savedStateHandle = savedStateHandle,
            parseFileUseCase = parseFileUseCase,
            downloadFileUseCase = downloadFileUseCase
        )

        coEvery { downloadFileUseCase.run(any(), any()) } just Runs
        coEvery { parseFileUseCase.run(any(), any(), any()) } returns expectedList

        val url = "An Url"
        runTest {
            underTest.downloadAndParse(url)

            //Check all States
            underTest.uiState.test {
                assertEquals(UiState.Idle, awaitItem())
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(UiState.Success(expectedList), awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

        //Check if the Mocks were called
        coVerify { downloadFileUseCase.run(url, any()) }
        coVerify { parseFileUseCase.run(any(), any(), any()) }
    }

    @Test
    fun `when request a new download and some exception raises then ui state is Error`() {
        stubSavedStateHandler(
            uiState = null,
            session = null
        )

        underTest = MainViewModel(
            context = context,
            savedStateHandle = savedStateHandle,
            parseFileUseCase = parseFileUseCase,
            downloadFileUseCase = downloadFileUseCase
        )

        val exception = RuntimeException("Mocked Exception")

        coEvery { downloadFileUseCase.run(any(), any()) } just Runs
        coEvery { parseFileUseCase.run(any(), any(), any()) } throws exception

        val url = "An Url"
        runTest {
            underTest.downloadAndParse(url)

            //Check all States
            underTest.uiState.test {
                assertEquals(UiState.Idle, awaitItem())
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(UiState.Error(exception.message!!), awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

        //Check if the Mocks were called
        coVerify { downloadFileUseCase.run(url, any()) }
        coVerify { parseFileUseCase.run(any(), any(), any()) }
    }

    @Test
    fun `when request a more data and parser returns valid data then ui state is success and lists are added`() {
        val startingList = listOf(
            User(
                firstName = "William",
                surname = "Miranda",
                issueCount = 42,
                dateOfBirth = "23/03/1985",
                avatar = "http://avatar.url"
            )
        )

        val newList = listOf(
            User(
                firstName = "First",
                surname = "Last",
                issueCount = 12,
                dateOfBirth = "10/02/1989",
                avatar = "http://avatar2.url"
            )
        )

        //This will skip the Idle Status
        stubSavedStateHandler(
            uiState = UiState.Success(startingList),
            session = "MySession"
        )

        underTest = MainViewModel(
            context = context,
            savedStateHandle = savedStateHandle,
            parseFileUseCase = parseFileUseCase,
            downloadFileUseCase = downloadFileUseCase
        )

        coEvery { downloadFileUseCase.run(any(), any()) } just Runs
        coEvery { parseFileUseCase.run(any(), any(), any()) } returns newList

        runTest {
            underTest.downloadMore()

            //Check all States
            underTest.uiState.test {
                assertEquals(UiState.Success(startingList), awaitItem())
                assertEquals(UiState.Success(startingList + newList), awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

        //Check if the Mocks were called
        coVerify(exactly = 0) { downloadFileUseCase.run(any(), any()) }
        coVerify { parseFileUseCase.run(any(), any(), any()) }
    }

    @Test
    fun `when request a more data and parser raises an exception then ui state state is error`() {
        val startingList = listOf(
            User(
                firstName = "William",
                surname = "Miranda",
                issueCount = 42,
                dateOfBirth = "23/03/1985",
                avatar = "http://avatar.url"
            )
        )

        //This will skip the Idle Status
        stubSavedStateHandler(
            uiState = UiState.Success(startingList),
            session = "MySession"
        )

        underTest = MainViewModel(
            context = context,
            savedStateHandle = savedStateHandle,
            parseFileUseCase = parseFileUseCase,
            downloadFileUseCase = downloadFileUseCase
        )

        val exception = RuntimeException("Mocked Exception")

        coEvery { downloadFileUseCase.run(any(), any()) } just Runs
        coEvery { parseFileUseCase.run(any(), any(), any()) } throws exception

        runTest {
            underTest.downloadMore()

            //Check all States
            underTest.uiState.test {
                assertEquals(UiState.Success(startingList), awaitItem())
                assertEquals(UiState.Error(exception.message!!), awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

        //Check if the Mocks were called
        coVerify(exactly = 0) { downloadFileUseCase.run(any(), any()) }
        coVerify { parseFileUseCase.run(any(), any(), any()) }
    }

    private fun stubSavedStateHandler(
        uiState: UiState?,
        session: String?
    ) {
        val STATE_KEY = "State"
        val SESSION_KEY = "Session"

        every { savedStateHandle.get<UiState>(STATE_KEY) } returns uiState
        every { savedStateHandle.get<String>(SESSION_KEY) } returns session
    }
}