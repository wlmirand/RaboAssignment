package william.miranda.csvparser.adapter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class IntTypeAdapterTest {

    lateinit var underTest: IntTypeAdapter

    @Before
    fun setup() {
        underTest = IntTypeAdapter()
    }

    @Test
    fun `when get a correct value then convert it`() {
        val value = "42"
        val result = underTest.convert(value)

        assertEquals(value.toInt(), result)
    }

    @Test
    fun `when get a bad value then get an exception`() {
        val value = "42a"

        assertThrows(NumberFormatException::class.java) {
            underTest.convert(value)
        }
    }
}