package william.miranda.csvparser

import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import william.miranda.csvparser.adapters.CsvTypeAdapter
import william.miranda.csvparser.parser.CsvParser
import java.io.File

class CsvParserTest {

    private lateinit var underTest: CsvParser

    @Before
    fun setup() {
        underTest = CsvParser()
    }

    @Test
    fun bla() {
        val sessionName = "MySession"
        val file = mockk<File>()
        val separator = ','

        val result = underTest.createSession(
            sessionName = sessionName,
            file = file,
            separator = separator
        )
    }
}

private val booleanTypeAdapter = object : CsvTypeAdapter<Boolean> {
    override fun convert(value: String): Boolean {
        return when (value) {
            "true" -> true
            "false" -> false
            else -> throw RuntimeException("Bad Boolean")
        }
    }
}