package william.miranda.csvparser.parser

import william.miranda.csvparser.adapter.CsvTypeAdapter
import william.miranda.csvparser.exceptions.ColumnSizeMismatchException
import william.miranda.csvparser.exceptions.EmptyHeaderException
import william.miranda.csvparser.exceptions.FileNotExistsException
import william.miranda.csvparser.exceptions.FileNotOpenException
import william.miranda.csvparser.exceptions.NoCsvFieldAnnotationException
import william.miranda.csvparser.exceptions.NoHeaderMatchException
import william.miranda.csvparser.exceptions.NoPrimaryConstructorException
import william.miranda.csvparser.exceptions.NoTypeAdapterRegisteredException
import java.io.BufferedReader
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

class ParsingSession(
    val name: String,
    val file: File,
    val separator: Char,
    private val adapters: Map<KType, CsvTypeAdapter<Any>>
) {
    private companion object {
        private const val DOUBLE_QUOTES = '"'
    }

    /**
     * Keep track of the Status
     */
    var status: SessionStatus = SessionStatus.ONGOING
        private set

    /**
     * Buffered Reader used to read the Stream
     * Since we are reading data in a paged way, we will keep this open while we are reading the data
     */
    private var fileReader: BufferedReader? = null

    /**
     * Keep track of the Header
     */
    private var header: List<String>? = null
    private val headerSize: Int
        get() = header?.size ?: 0

    init {
        openFile()
    }

    /**
     * Open the File and gets the Reader ready
     * This should be called before the parse
     */
    private fun openFile() {
        //File does not exists
        if (!file.exists()) {
            throw FileNotExistsException(file.absolutePath)
        }

        fileReader = file.bufferedReader()
        parseHeader()
    }

    /**
     * Close everything and reset variables
     */
    private fun closeFile() {
        fileReader?.close()
        fileReader = null
        header = null
        status = SessionStatus.FINISHED
    }

    /**
     * Makes the Header ready for parsing the Records
     */
    private fun parseHeader() {
        //Gets a valid reader
        val reader = fileReader ?: throw FileNotOpenException()

        //When opening the File, already keep the Header Ready
        header = parseHeaderLine(
            line = reader.readLine(),
            separator = separator
        )
    }

    /**
     * Parse a Given Line as the Header
     * Also, clear the double quotes inside the values if we have them
     */
    private fun parseHeaderLine(
        line: String,
        separator: Char
    ): List<String> {
        val header = line.split(separator)
            .map { headerItem ->
                headerItem.filterNot { it == DOUBLE_QUOTES }
            }

        if (header.isEmpty()) {
            throw EmptyHeaderException()
        }

        return header
    }

    /**
     * Entry point for getting the Parsed Records
     * We should keep this Synchronized because multiple threads reading data may lead to issues
     */
    @Synchronized
    fun <T : Any> parseRecords(
        pageSize: Int,
        skipMalformedRecords: Boolean = false,
        clazz: KClass<T>
    ): List<T> {
        //We are done
        if (status == SessionStatus.FINISHED) {
            return emptyList()
        }

        //Gets a valid reader
        val reader = fileReader ?: throw FileNotOpenException()

        //Data structure for the output
        val records = mutableListOf<T>()

        //Go parse a "page" of data
        while (records.size < pageSize) {
            val line = reader.readLine()

            //File ended, close the Reader and sign that we are done
            if (line == null) {
                closeFile()
                break
            }

            //Blank line, skip it
            if (line.isBlank()) {
                continue
            }

            //Get the Line with the data
            val record = parseRecord(
                line = line,
                separator = separator,
                headerSize = headerSize,
                skipMalformedRecords = skipMalformedRecords
            )

            //Create the Model
            val model = createModel(
                header = header ?: throw EmptyHeaderException(),
                record = record,
                clazz = clazz
            )

            //Add to the List
            model?.let { records.add(it) }
        }

        return records
    }

    /**
     * Parse a Given Line as a Record
     */
    private fun parseRecord(
        line: String,
        separator: Char,
        headerSize: Int,
        skipMalformedRecords: Boolean
    ): List<String>? {
        return line.split(separator) //Breaks the Record Columns
            .map {
                it.filterNot { it == DOUBLE_QUOTES } //Clear double quotes
            }.also { record ->
                //Ensure Column size matches the Header
                if (record.size != headerSize) {
                    if (!skipMalformedRecords) {
                        throw ColumnSizeMismatchException(line, headerSize)
                    } else {
                        return null
                    }
                }
            }
    }

    /**
     * Create a class of the Data Model and put the Record data into it
     * Lets assume we have a simple Data Class where parameters annotated with @CsvField
     * are passed into the primary constructor.
     * The CsvField annotation contains the matching name from the Header that tell us
     * how to Map from Csv Header to the class attribute
     * On this implementation, all data class parameters should be annotated
     */
    private fun <T : Any> createModel(
        header: List<String>,
        record: List<String>?,
        clazz: KClass<T>
    ): T? {
        if (record == null) {
            return null
        }

        //Lets get the primary constructor for our class
        val primaryConstructor =
            clazz.primaryConstructor ?: throw NoPrimaryConstructorException()

        //Fetch the parameters and init the Constructor array of parameters
        val parameters = primaryConstructor.parameters
        val constructorParameters = Array<Any?>(parameters.size) { null }

        for (parameter in parameters) {
            //Get the Type Adapter for the parameter type
            val adapter =
                adapters[parameter.type]
                    ?: throw NoTypeAdapterRegisteredException(parameter.type.toString())

            //Go get the Annotation so we know which Column to match
            val csvFieldAnnotation =
                parameter.annotations.firstOrNull { it is CsvField } as? CsvField
                    ?: throw NoCsvFieldAnnotationException(parameter.name.toString())

            //Get the index of the column that matches the field name
            val index = header.indexOfFirst { it == csvFieldAnnotation.fieldName }
            if (index == -1) {
                throw NoHeaderMatchException(csvFieldAnnotation.fieldName)
            }

            //Convert the Value using the Registered TypeAdapter
            //Let any Exception that raises here to be propagated and Handled by the caller
            val convertedValue = adapter.convert(record[index])

            //Assign the converted value into the constructor parameters
            constructorParameters[index] = convertedValue
        }

        //Return the Model
        return primaryConstructor.call(*constructorParameters)
    }

    /**
     * Type for the Session status
     */
    enum class SessionStatus {
        ONGOING,
        FINISHED
    }
}