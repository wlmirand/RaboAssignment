package william.miranda.csvparser.adapter

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StringTypeAdapterTest {

    private lateinit var underTest: StringTypeAdapter

    @Before
    fun setup() {
        underTest = StringTypeAdapter()
    }

    @Test
    fun `when get a correct value then convert it`() {
        val value = "A String Value"
        val result = underTest.convert(value)

        assertEquals(value, result)
    }
}