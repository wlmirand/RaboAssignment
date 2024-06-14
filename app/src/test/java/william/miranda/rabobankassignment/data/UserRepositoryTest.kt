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
import org.joda.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import william.miranda.csvparser.parser.CsvParser
import william.miranda.csvparser.parser.ParsingSession
import william.miranda.rabobankassignment.data.model.UserModel
import java.io.File
import kotlin.reflect.KClass

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
    fun `when csvParser contains a session and it is not finished then request new records`() {
        val session = mockk<ParsingSession>()
        val file = mockk<File>()
        val expectedList = listOf(
            UserModel(
                firstName = "First",
                surname = "Last",
                issueCount = 4,
                dateOfBirth = LocalDateTime(1985,3,23,12,30,0),
                avatar = "avatar"
            )
        )

        every { session.status } returns ParsingSession.SessionStatus.ONGOING
        every {
            session.parseRecords(
                pageSize = any(),
                skipMalformedRecords = any(),
                clazz = any<KClass<UserModel>>()
            )
        } returns expectedList

        every { csvParser.getSession(any()) } returns session

        runTest {
            val result = underTest.getUsers(
                sessionName = "A Session",
                file = file,
                pageSize = 50
            )

            assertEquals(result, expectedList)
        }

        verify {
            session.parseRecords(
                pageSize = any(),
                skipMalformedRecords = any(),
                UserModel::class
            )
        }
    }

    @Test
    fun `when csvParser does not contains a session then create one and if it is not finished then request new records`() {
        val session = mockk<ParsingSession>()
        val file = mockk<File>()
        val expectedList = listOf(
            UserModel(
                firstName = "First",
                surname = "Last",
                issueCount = 4,
                dateOfBirth = LocalDateTime(1985,3,23,12,30,0),
                avatar = "avatar"
            )
        )

        every { session.status } returns ParsingSession.SessionStatus.ONGOING
        every {
            session.parseRecords(
                pageSize = any(),
                skipMalformedRecords = any(),
                clazz = any<KClass<UserModel>>()
            )
        } returns expectedList

        every { csvParser.getSession(any()) } returns null

        every {
            csvParser.createSession(
                sessionName = any(),
                file = any(),
                separator = any()
            )
        } returns session

        runTest {
            val result = underTest.getUsers(
                sessionName = "A Session",
                file = file,
                pageSize = 50
            )

            assertEquals(result, expectedList)
        }

        verify {
            session.parseRecords(
                pageSize = any(),
                skipMalformedRecords = any(),
                UserModel::class
            )
        }
    }

    @Test
    fun `when csvParser contains a session and it is finished then return empty list`() {
        val session = mockk<ParsingSession>()
        val file = mockk<File>()
        val returnList = listOf(
            UserModel(
                firstName = "First",
                surname = "Last",
                issueCount = 4,
                dateOfBirth = LocalDateTime(1985,3,23,12,30,0),
                avatar = "avatar"
            )
        )

        every { session.status } returns ParsingSession.SessionStatus.FINISHED
        every {
            session.parseRecords(
                pageSize = any(),
                skipMalformedRecords = any(),
                clazz = any<KClass<UserModel>>()
            )
        } returns returnList

        every { csvParser.getSession(any()) } returns session

        runTest {
            val result = underTest.getUsers(
                sessionName = "A Session",
                file = file,
                pageSize = 50
            )

            assertEquals(result, emptyList<UserModel>())
        }

        verify(exactly = 0) {
            session.parseRecords(
                pageSize = any(),
                skipMalformedRecords = any(),
                UserModel::class
            )
        }
    }
}