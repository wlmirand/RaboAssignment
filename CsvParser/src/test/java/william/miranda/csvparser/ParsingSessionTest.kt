package william.miranda.csvparser

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import william.miranda.csvparser.adapter.CsvTypeAdapter
import william.miranda.csvparser.exceptions.ColumnSizeMismatchException
import william.miranda.csvparser.exceptions.FileNotExistsException
import william.miranda.csvparser.exceptions.NoCsvFieldAnnotationException
import william.miranda.csvparser.exceptions.NoHeaderMatchException
import william.miranda.csvparser.exceptions.NoTypeAdapterRegisteredException
import william.miranda.csvparser.parser.CsvField
import william.miranda.csvparser.parser.ParsingSession
import java.io.File
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

class ParsingSessionTest {

    private companion object {
        private const val VALID_CSV = "Valid.csv"
        private const val NO_HEADER_CSV = "NoHeader.csv"
        private const val MISSING_COLUMNS = "MissingColumns.csv"
        private const val TYPE_MISMATCH = "TypeMismatch.csv"
        private const val BIG_VALID = "BigValid.csv"
    }

    @MockK
    private lateinit var adapters: Map<KType, CsvTypeAdapter<Any>>

    private val separator = ','

    private lateinit var underTest: ParsingSession

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { adapters[String::class.starProjectedType] } returns stringTypeAdapter
        every { adapters[Int::class.starProjectedType] } returns intTypeAdapter
    }

    @Test
    fun `when the file does not exist Then Get the Exception`() {
        every { adapters[Boolean::class.starProjectedType] } returns booleanTypeAdapter
        val file = File("/sdcard/FileNotExist.csv")

        val exception = assertThrows(FileNotExistsException::class.java) {
            underTest = ParsingSession(
                name = "MySession",
                file = file,
                separator = separator,
                adapters = adapters
            )
        }

        assertTrue(exception.message!!.contains("FileNotExist.csv"))
    }

    @Test
    fun `when Csv Is Valid And Model Is Valid Then Parse With Success`() {
        val file = File(javaClass.classLoader!!.getResource(VALID_CSV).toURI())
        every { adapters[Boolean::class.starProjectedType] } returns booleanTypeAdapter

        underTest = ParsingSession(
            name = "MySession",
            file = file,
            separator = separator,
            adapters = adapters
        )

        val result = underTest.parseRecords(
            pageSize = 50,
            skipMalformedRecords = false,
            clazz = ValidDataModel::class
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
        val file = File(javaClass.classLoader!!.getResource(VALID_CSV).toURI())
        every { adapters[Boolean::class.starProjectedType] } returns booleanTypeAdapter

        underTest = ParsingSession(
            name = "MySession",
            file = file,
            separator = separator,
            adapters = adapters
        )

        val exception = assertThrows(NoCsvFieldAnnotationException::class.java) {
            underTest.parseRecords(
                pageSize = 50,
                skipMalformedRecords = false,
                clazz = NotAnnotatedDataModel::class
            )
        }

        assertTrue(exception.message!!.contains("age"))
    }

    @Test
    fun `when a needed Type Adapter is not registered Then Get An Exception`() {
        val file = File(javaClass.classLoader!!.getResource(VALID_CSV).toURI())
        every { adapters[Boolean::class.starProjectedType] } returns null

        underTest = ParsingSession(
            name = "MySession",
            file = file,
            separator = separator,
            adapters = adapters
        )

        val exception = assertThrows(NoTypeAdapterRegisteredException::class.java) {
            underTest.parseRecords(
                pageSize = 50,
                skipMalformedRecords = false,
                clazz = ValidDataModel::class
            )
        }

        assertTrue(exception.message!!.contains("Boolean"))
    }

    @Test
    fun `when annotated field does not match a Header column Then Get An Exception`() {
        val file = File(javaClass.classLoader!!.getResource(VALID_CSV).toURI())
        every { adapters[Boolean::class.starProjectedType] } returns booleanTypeAdapter

        underTest = ParsingSession(
            name = "MySession",
            file = file,
            separator = separator,
            adapters = adapters
        )

        val exception = assertThrows(NoHeaderMatchException::class.java) {
            underTest.parseRecords(
                pageSize = 50,
                skipMalformedRecords = false,
                clazz = BadHeaderDataModel::class
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
        val file = File(javaClass.classLoader!!.getResource(NO_HEADER_CSV).toURI())
        every { adapters[Boolean::class.starProjectedType] } returns booleanTypeAdapter

        underTest = ParsingSession(
            name = "MySession",
            file = file,
            separator = separator,
            adapters = adapters
        )

        val exception = assertThrows(NoHeaderMatchException::class.java) {
            underTest.parseRecords(
                pageSize = 50,
                clazz = ValidDataModel::class
            )
        }

        assertTrue(exception.message!!.contains("Name"))
    }

    @Test
    fun `when Csv Columns Size does not match Then Get an Exception`() {
        val file = File(javaClass.classLoader!!.getResource(MISSING_COLUMNS).toURI())
        every { adapters[Boolean::class.starProjectedType] } returns booleanTypeAdapter

        underTest = ParsingSession(
            name = "MySession",
            file = file,
            separator = separator,
            adapters = adapters
        )

        val exception = assertThrows(ColumnSizeMismatchException::class.java) {
            underTest.parseRecords(
                pageSize = 50,
                skipMalformedRecords = false,
                clazz = ValidDataModel::class
            )
        }

        assertTrue(exception.message!!.contains("Expected 3 parameters"))
    }

    @Test
    fun `when Csv Columns Size does not match but skip flag is enabled, Then ignore the field`() {
        val file = File(javaClass.classLoader!!.getResource(MISSING_COLUMNS).toURI())
        every { adapters[Boolean::class.starProjectedType] } returns booleanTypeAdapter

        underTest = ParsingSession(
            name = "MySession",
            file = file,
            separator = separator,
            adapters = adapters
        )

        val result = underTest.parseRecords(
            pageSize = 50,
            skipMalformedRecords = true,
            clazz = ValidDataModel::class
        )

        assertEquals(result.size, 1)
        assertEquals(result[0].name, "William")
        assertEquals(result[0].age, 38)
        assertEquals(result[0].isMan, true)
    }

    @Test
    fun `when the TypeAdapter raises an exception Then Get the Exception`() {
        every { adapters[Boolean::class.starProjectedType] } returns booleanTypeAdapter
        val file = File(javaClass.classLoader!!.getResource(TYPE_MISMATCH).toURI())

        underTest = ParsingSession(
            name = "MySession",
            file = file,
            separator = separator,
            adapters = adapters
        )

        assertThrows(NumberFormatException::class.java) {
            underTest.parseRecords(
                pageSize = 50,
                clazz = ValidDataModel::class
            )
        }
    }

    @Test
    fun `when Csv Is is Big, should fetch number of items from PAge Size`() {
        val file = File(javaClass.classLoader!!.getResource(BIG_VALID).toURI())
        every { adapters[Boolean::class.starProjectedType] } returns booleanTypeAdapter

        underTest = ParsingSession(
            name = "MySession",
            file = file,
            separator = separator,
            adapters = adapters
        )

        //We have 10 records
        for (i in 1..3) {
            val result = underTest.parseRecords(
                pageSize = 3,
                skipMalformedRecords = false,
                clazz = ValidDataModel::class
            )

            assertEquals(result.size, 3)
        }

        //Now we consumed 3 * 3 = 9 records, there is 1 more left
        val result = underTest.parseRecords(
            pageSize = 6,
            skipMalformedRecords = false,
            clazz = ValidDataModel::class
        )

        assertEquals(result.size, 1)
    }

    @Test
    fun `when parsing was finished then return empty list`() {
        val file = File(javaClass.classLoader!!.getResource(BIG_VALID).toURI())
        every { adapters[Boolean::class.starProjectedType] } returns booleanTypeAdapter

        underTest = ParsingSession(
            name = "MySession",
            file = file,
            separator = separator,
            adapters = adapters
        )

        //We have 10 records, so clear it in one page
        val result10 = underTest.parseRecords(
            pageSize = 10,
            skipMalformedRecords = false,
            clazz = ValidDataModel::class
        )

        assertEquals(result10.size, 10)

        //Nothing left
        val resultEmpty = underTest.parseRecords(
            pageSize = 6,
            skipMalformedRecords = false,
            clazz = ValidDataModel::class
        )

        assertTrue(resultEmpty.isEmpty())
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

private val stringTypeAdapter = object : CsvTypeAdapter<String> {
    override fun convert(value: String) = value
}

private val intTypeAdapter = object : CsvTypeAdapter<Int> {
    override fun convert(value: String) = value.toInt()
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