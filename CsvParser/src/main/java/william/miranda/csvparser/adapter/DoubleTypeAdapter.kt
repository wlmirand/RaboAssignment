package william.miranda.csvparser.adapter

class DoubleTypeAdapter: CsvTypeAdapter<Double> {

    override fun convert(value: String): Double {
        return value.toDouble()
    }
}