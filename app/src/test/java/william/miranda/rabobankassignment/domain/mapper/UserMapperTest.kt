package william.miranda.rabobankassignment.domain.mapper

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.joda.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import william.miranda.rabobankassignment.data.model.UserModel
import william.miranda.rabobankassignment.domain.formatter.DateFormatter

class UserMapperTest {

    private lateinit var underTest: UserMapper

    @MockK
    private lateinit var dateFormatter: DateFormatter

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        underTest = UserMapper(
            dateFormatter = dateFormatter
        )
    }

    @Test
    fun `when UserModel is provided then it is converted to User`() {
        val birthDate = LocalDateTime(1985, 3, 23, 12, 30, 0)
        val convertedBirthDate = "23/03/1985"

        every { dateFormatter.format(birthDate) } returns convertedBirthDate

        val userModel = UserModel(
            firstName = "William",
            surname = "Miranda",
            issueCount = 42,
            dateOfBirth = birthDate,
            avatar = "http://avatar.url"
        )

        val result = underTest.map(userModel)

        verify { dateFormatter.format(birthDate) }
        assertEquals(result.firstName, userModel.firstName)
        assertEquals(result.surname, userModel.surname)
        assertEquals(result.issueCount, userModel.issueCount)
        assertEquals(result.dateOfBirth, convertedBirthDate)
        assertEquals(result.avatar, userModel.avatar)
    }
}