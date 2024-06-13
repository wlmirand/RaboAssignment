package william.miranda.rabobankassignment.domain.usecase

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.joda.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import william.miranda.rabobankassignment.data.UserRepository
import william.miranda.rabobankassignment.data.model.UserModel
import william.miranda.rabobankassignment.domain.mapper.UserMapper
import william.miranda.rabobankassignment.domain.model.User

class ParseFileUseCaseTest {

    private lateinit var underTest: ParseFileUseCase

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var userMapper: UserMapper

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        underTest = ParseFileUseCase(
            userRepository = userRepository,
            userMapper = userMapper
        )
    }

    @Test
    fun `when Use Case is called Then Repository and Mapper are called`() {
        val birthDate = LocalDateTime(1985, 3, 23, 12, 30, 0)
        val userModel = UserModel(
            firstName = "William",
            surname = "Miranda",
            issueCount = 42,
            dateOfBirth = birthDate,
            avatar = "http://avatar.url"
        )
        val user = User(
            firstName = "William",
            surname = "Miranda",
            issueCount = 42,
            dateOfBirth = "23/03/1985",
            avatar = "http://avatar.url"
        )

        val repoList = listOf(userModel)

        every { userMapper.map(any()) } returns user
        coEvery { userRepository.getUsers(any()) } returns repoList

        val urlString = "http://some.url"

        runTest {
            val result = underTest.run(urlString)

            assertEquals(result.size, repoList.size)
            assertEquals(result[0].firstName, repoList[0].firstName)
            assertEquals(result[0].surname, repoList[0].surname)
            assertEquals(result[0].issueCount, repoList[0].issueCount)
            assertEquals(result[0].dateOfBirth, user.dateOfBirth)
            assertEquals(result[0].avatar, repoList[0].avatar)
        }
    }
}