package william.miranda.csvparser.adapters

class StringTypeAdapter: CsvTypeAdapter<String> {

    override fun convert(value: String): String {
        return value
    }
}