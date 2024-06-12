package william.miranda.csvparser.adapters

/**
 * Interface that defines the TypeAdapters, to convert a String to a given type
 * Note that we may want to convert some string to a Null value
 */
interface CsvTypeAdapter<out T> {

    /**
     * Converts the String parsed from the CSV to the specified Data Type
     */
    fun convert(value: String): T?
}