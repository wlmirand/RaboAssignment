package william.miranda.csvparser

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import william.miranda.csvparser.parser.CsvParser
import william.miranda.csvparser.parser.ParsingSession
import java.io.File

class CsvParserTest {

    private companion object {
        private const val VALID_CSV = "Valid.csv"
    }

    private lateinit var underTest: CsvParser

    @Before
    fun setup() {
        underTest = CsvParser()
    }

    @Test
    fun `when a session is created, it is stored into the data structure and session is started`() {
        val sessionName = "MySession"
        val file = File(javaClass.classLoader!!.getResource(VALID_CSV).toURI())
        val separator = ','

        val sessionCreated = underTest.createSession(
            sessionName = sessionName,
            file = file,
            separator = separator
        )

        val sessionRetrieved = underTest.getSession(sessionName)

        assertEquals(sessionCreated, sessionRetrieved)
        assertEquals(sessionCreated.status, ParsingSession.SessionStatus.ONGOING)
        assertEquals(sessionCreated.name, sessionName)
    }
}
