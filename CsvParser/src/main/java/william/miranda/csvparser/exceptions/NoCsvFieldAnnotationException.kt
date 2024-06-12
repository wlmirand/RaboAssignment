package william.miranda.csvparser.exceptions

class NoCsvFieldAnnotationException(field: String) :
    IllegalStateException("$NO_CSV_FIELD_ANNOTATION $field") {

    private companion object {
        private const val NO_CSV_FIELD_ANNOTATION = "No CsvField annotation for:"
    }

}