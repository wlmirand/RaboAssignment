package william.miranda.rabobankassignment.data

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import william.miranda.csvparser.parser.CsvParser
import william.miranda.rabobankassignment.data.model.UserModel
import java.io.InputStream
import java.net.URL

class UserRepositoryTest {

    private lateinit var underTest: UserRepository

    @MockK
    private lateinit var csvParser: CsvParser

    private val dispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(dispatcher)
        underTest = UserRepository(
            csvParser = csvParser,
            dispatcher = dispatcher
        )
    }

    @Test
    fun `when csvParser returns a valid list then the repo returns a valid list`() {
        val url = mockk<URL>()
        val inputStream = mockk<InputStream>()
        val expectedList = emptyList<UserModel>()

        every { url.openStream() } returns inputStream
        every {
            csvParser.parseRecords(
                inputStream = inputStream,
                separator = ',',
                UserModel::class
            )
        } returns expectedList

        runTest {
            val result = underTest.getUsers(url = url)
            assertEquals(result, expectedList)
        }

        verify {
            csvParser.parseRecords(
                inputStream = inputStream,
                separator = ',',
                UserModel::class
            )
        }
    }

    @Test
    fun `when csvParser throws an exception then the repository raises it`() {
        val url = mockk<URL>()
        val inputStream = mockk<InputStream>()
        val expectedException = RuntimeException()

        every { url.openStream() } returns inputStream
        every {
            csvParser.parseRecords(
                inputStream = inputStream,
                separator = ',',
                UserModel::class
            )
        } throws expectedException

        assertThrows(RuntimeException::class.java) {
            runTest {
                underTest.getUsers(url = url)
            }
        }

        verify {
            csvParser.parseRecords(
                inputStream = inputStream,
                separator = ',',
                UserModel::class
            )
        }
    }
}