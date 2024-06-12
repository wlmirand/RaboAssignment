package william.miranda.csvparser

import william.miranda.csvparser.adapters.CsvTypeAdapter
import william.miranda.csvparser.adapters.DoubleTypeAdapter
import william.miranda.csvparser.adapters.IntTypeAdapter
import william.miranda.csvparser.adapters.StringTypeAdapter
import william.miranda.csvparser.exceptions.ColumnSizeMismatchException
import william.miranda.csvparser.exceptions.NoCsvFieldAnnotationException
import william.miranda.csvparser.exceptions.NoHeaderMatchException
import william.miranda.csvparser.exceptions.NoPrimaryConstructorException
import william.miranda.csvparser.exceptions.NoTypeAdapterRegisteredException
import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType

/**
 * Class that holds the logic to parse the a CSV File
 * THe main goal is:
 * - Parse the CSV file and keep track of the Header and the Records (@see CsvClass)
 * - Receive a Data Model class
 * - Put the CsvData inside a DataModel instance
 */
class CsvParser {

    /**
     * Container for all Type adapters that our Parser knows
     * We use this to use an instance of CsvTypeAdapter for a given type
     */
    private val adapters = mutableMapOf<KType, CsvTypeAdapter<Any>>()

    /**
     * Just add some default basic adapters
     * Note these may be replaced
     */
    init {
        registerTypeAdapter(String::class, StringTypeAdapter())
        registerTypeAdapter(Int::class, IntTypeAdapter())
        registerTypeAdapter(Double::class, DoubleTypeAdapter())
    }

    /**
     * Entry point for the Library.
     * This method wraps the following operations:
     * - Retrieve the Csv file
     * - Put the Csv contents inside the CsvData class (Header + Records)
     * - Map the CsvData to List<Model> to return a nice list of a custom Model class
     */
    fun <T : Any> downloadAndParse(
        inputStream: InputStream,
        separator: Char,
        clazz: KClass<T>
    ): List<T> {
        //Get the CsvData
        val csvData = parseFile(
            inputStream = inputStream,
            separator = separator
        )

        //Get the Model classes
        return convert(
            csvData = csvData,
            clazz = clazz
        )
    }

    /**
     * Method to take the Input Stream and return an instance of CsvData
     * For now we do not worry about handling the specific types
     * Here we ensure the number of columns match between Header and all the Records
     */
    private fun parseFile(
        inputStream: InputStream,
        separator: Char
    ): CsvData {
        //Read the Data
        val reader = inputStream.bufferedReader()

        //Lets take the 1st line and split based on the separator
        //Lets clear the double quotes inside the values if we have them
        val header = reader.readLine()
            .split(separator)
            .map { line -> line.filterNot { it == '"' } }

        val headerSize = header.size

        //Now take the records
        //Also, clear the double quotes inside the values if we have them
        val records = reader.lineSequence()
            .filter { it.isNotBlank() } //Ignore empty lines
            .map { line ->
                line.split(separator) //Breaks the Record Columns
                    .map {
                    it.filterNot { it == '"' } //Clear double quotes
                }.also { record ->
                    //Ensure Column size matches the Header
                    if (record.size != headerSize) {
                        throw ColumnSizeMismatchException(line, headerSize)
                    }
                }
            }
            .toList()

        //Lets put everything inside our Data Structure
        return CsvData(
            header = header,
            records = records
        )
    }

    /**
     * Entry point to fill the CsvData inside a pretty class
     */
    private fun <T : Any> convert(
        csvData: CsvData,
        clazz: KClass<T>
    ): List<T> {

        //Create the output List
        val recordList = mutableListOf<T>()

        //Lets iterate through all the records
        csvData.records.forEach { record ->

            //For each record, we should create a new Model
            createModel(
                header = csvData.header,
                record = record,
                clazz = clazz
            ).also { recordList.add(it) }
        }

        //Return the final list
        return recordList
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
        record: List<String>,
        clazz: KClass<T>
    ): T {
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
     * Add a TypeAdapter to the Parser
     * Would be nice to having a Generic method, but the method then would have to be
     * inline fun <reified T>, which would not be able to access our private adapters val.
     * So either we keep this not so nice method signature, or we expose the adapters, which is not good too
     */
    fun registerTypeAdapter(clazz: KClass<*>, adapter: CsvTypeAdapter<Any>) {
        adapters[clazz.starProjectedType] = adapter
    }
}