package william.miranda.csvparser.adapter

/**
 * Simple Int Adapter
 */
class IntTypeAdapter: CsvTypeAdapter<Int> {

    /**
     * From here we just try to convert the String to an Int
     * An Exception will be raised if the provided String is not an Integer
     * But we leave it to the caller to handle
     */
    override fun convert(value: String): Int {
        return value.toInt()
    }
}