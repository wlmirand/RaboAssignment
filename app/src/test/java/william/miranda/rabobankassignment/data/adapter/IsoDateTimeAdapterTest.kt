package william.miranda.rabobankassignment.data.adapter

import org.joda.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class IsoDateTimeAdapterTest {

    private lateinit var underTest: IsoDateTimeAdapter

    @Before
    fun setup() {
        underTest = IsoDateTimeAdapter()
    }

    @Test
    fun `when date time respects the expected pattern then conversion is done with success`() {
        val dateTimeString = "1985-03-23T12:30:00"
        val dateTime = underTest.convert(dateTimeString)
        val expected = LocalDateTime(1985, 3, 23, 12, 30, 0)

        assertEquals(dateTime, expected)
    }

    @Test
    fun `when date time does not respects the correct pattern then we get an exception`() {
        val dateTimeString = "1985-03-23"

        assertThrows(IllegalArgumentException::class.java) {
            underTest.convert(dateTimeString)
        }
    }
}