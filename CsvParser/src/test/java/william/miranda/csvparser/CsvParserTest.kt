package william.miranda.csvparser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import william.miranda.csvparser.adapters.CsvTypeAdapter
import william.miranda.csvparser.exceptions.ColumnSizeMismatchException
import william.miranda.csvparser.exceptions.NoCsvFieldAnnotationException
import william.miranda.csvparser.exceptions.NoHeaderMatchException
import william.miranda.csvparser.exceptions.NoTypeAdapterRegisteredException
import william.miranda.csvparser.parser.CsvField
import william.miranda.csvparser.parser.CsvParser

class CsvParserTest {

    private companion object {
        private const val VALID_CSV = "Valid.csv"
        private const val NO_HEADER_CSV = "NoHeader.csv"
        private const val MISSING_COLUMNS = "MissingColumns.csv"
        private const val TYPE_MISMATCH = "TypeMismatch.csv"
    }

    private lateinit var underTest: CsvParser

    @Before
    fun setup() {
        underTest = CsvParser()
    }

    @Test
    fun `when Csv Is Valid And Model Is Valid Then Parse With Success`() {
        underTest.registerTypeAdapter(Boolean::class, booleanTypeAdapter)

        val stream = javaClass.classLoader!!.getResource(VALID_CSV).openStream()
        val result = underTest.parseRecords(
            inputStream = stream,
            separator = ',',
            ValidDataModel::class
        )

        assertEquals(result.size, 2)
        assertEquals(result[0].name, "William")
        assertEquals(result[0].age, 38)
        assertEquals(result[0].isMan, true)
        assertEquals(result[1].name, "Fernanda")
        assertEquals(result[1].age, 31)
        assertEquals(result[1].isMan, false)
    }

    @Test
    fun `when Csv Is Valid And Model Is Missing Annotations Then Get An Exception`() {
        underTest.registerTypeAdapter(Boolean::class, booleanTypeAdapter)

        val stream = javaClass.classLoader!!.getResource(VALID_CSV).openStream()

        val exception = assertThrows(NoCsvFieldAnnotationException::class.java) {
            underTest.parseRecords(
                inputStream = stream,
                separator = ',',
                NotAnnotatedDataModel::class
            )
        }

        assertTrue(exception.message!!.contains("age"))
    }

    @Test
    fun `when a needed Type Adapter is not registered Then Get An Exception`() {
        val stream = javaClass.classLoader!!.getResource(VALID_CSV).openStream()

        val exception = assertThrows(NoTypeAdapterRegisteredException::class.java) {
            underTest.parseRecords(
                inputStream = stream,
                separator = ',',
                ValidDataModel::class
            )
        }

        assertTrue(exception.message!!.contains("Boolean"))
    }

    @Test
    fun `when annotated field does not match a Header column Then Get An Exception`() {
        underTest.registerTypeAdapter(Boolean::class, booleanTypeAdapter)

        val stream = javaClass.classLoader!!.getResource(VALID_CSV).openStream()

        val exception = assertThrows(NoHeaderMatchException::class.java) {
            underTest.parseRecords(
                inputStream = stream,
                separator = ',',
                BadHeaderDataModel::class
            )
        }

        assertTrue(exception.message!!.contains("Men"))
    }

    /**
     * When the CSV has no Header, there no practical way to tell it is wrong.
     * Basically the code will assume the 1st row as the Header and will have the records
     * starting from Line 2. Often, the error will appear, because the CsvField annotations probably
     * will not match the values assumed to be the Header
     */
    @Test
    fun `when Csv Has No Headers Then Get an Exception`() {
        underTest.registerTypeAdapter(Boolean::class, booleanTypeAdapter)

        val stream = javaClass.classLoader!!.getResource(NO_HEADER_CSV).openStream()

        val exception = assertThrows(NoHeaderMatchException::class.java) {
            underTest.parseRecords(
                inputStream = stream,
                separator = ',',
                BadHeaderDataModel::class
            )
        }

        assertTrue(exception.message!!.contains("Name"))
    }

    @Test
    fun `when Csv Columns Size does not match Then Get an Exception`() {
        underTest.registerTypeAdapter(Boolean::class, booleanTypeAdapter)

        val stream = javaClass.classLoader!!.getResource(MISSING_COLUMNS).openStream()
        val exception = assertThrows(ColumnSizeMismatchException::class.java) {
            underTest.parseRecords(
                inputStream = stream,
                separator = ',',
                ValidDataModel::class
            )
        }

        assertTrue(exception.message!!.contains("Expected 3 parameters"))
    }

    @Test
    fun `when the TypeAdapter raises an exception Then Get the Exception`() {
        underTest.registerTypeAdapter(Boolean::class, booleanTypeAdapter)

        val stream = javaClass.classLoader!!.getResource(TYPE_MISMATCH).openStream()
        assertThrows(NumberFormatException::class.java) {
            underTest.parseRecords(
                inputStream = stream,
                separator = ',',
                ValidDataModel::class
            )
        }
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

data class ValidDataModel(
    @CsvField("Name") val name: String,
    @CsvField("Age") val age: Int,
    @CsvField("Is Man") val isMan: Boolean
)

data class NotAnnotatedDataModel(
    @CsvField("Name") val name: String,
    val age: Int, //Missing CsvField annotation here
    @CsvField("Is Man") val isMan: Boolean
)

data class BadHeaderDataModel(
    @CsvField("Name") val name: String,
    @CsvField("Age") val age: Int,
    @CsvField("Is Men") val isMan: Boolean
)