package william.miranda.csvparser.adapters

class DoubleTypeAdapter: CsvTypeAdapter<Double> {

    override fun convert(value: String): Double {
        return value.toDouble()
    }
}