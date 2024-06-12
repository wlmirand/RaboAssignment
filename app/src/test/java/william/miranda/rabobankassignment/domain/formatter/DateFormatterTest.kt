package william.miranda.rabobankassignment.domain.formatter

import org.joda.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DateFormatterTest {

    private lateinit var underTest: DateFormatter

    @Before
    fun setup() {
        underTest = DateFormatter()
    }

    @Test
    fun `when datetime is provided then convert to dd MM yyyy string`() {
        val expected = "23/03/1985"
        val dateTime = LocalDateTime(1985, 3, 23, 12, 30, 0)

        val result = underTest.format(dateTime = dateTime)

        assertEquals(result, expected)
    }
}