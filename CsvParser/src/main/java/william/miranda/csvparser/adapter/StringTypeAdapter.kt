package william.miranda.csvparser.adapter

/**
 * Simple String Adapter
 */
class StringTypeAdapter: CsvTypeAdapter<String> {

    /**
     * Since our parsed value is already a String, we just bypass it
     * Nothing can go wrong here
     */
    override fun convert(value: String): String {
        return value
    }
}