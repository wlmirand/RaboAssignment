package william.miranda.csvparser.adapters

class IntTypeAdapter: CsvTypeAdapter<Int> {

    override fun convert(value: String): Int {
        return value.toInt()
    }
}