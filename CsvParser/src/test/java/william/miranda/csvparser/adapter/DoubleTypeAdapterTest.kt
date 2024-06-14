package william.miranda.csvparser.adapter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class DoubleTypeAdapterTest {

    lateinit var underTest: DoubleTypeAdapter

    @Before
    fun setup() {
        underTest = DoubleTypeAdapter()
    }

    @Test
    fun `when get a correct value then convert it`() {
        val value = "42.45"
        val result = underTest.convert(value)

        assertEquals(value.toDouble(), result, 0.0001)
    }

    @Test
    fun `when get a bad value then get an exception`() {
        val value = "42a"

        assertThrows(NumberFormatException::class.java) {
            underTest.convert(value)
        }
    }
}