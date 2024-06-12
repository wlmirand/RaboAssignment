package william.miranda.csvparser.exceptions

class ColumnSizeMismatchException(
    line: String,
    expected: Int
) :
    IllegalStateException("$COLUMN_SIZE_NOT_MATCHING $line. $EXPECTED $expected $PARAMETERS") {

    private companion object {
        private const val COLUMN_SIZE_NOT_MATCHING = "Column size not matching on line:"
        private const val EXPECTED = "Expected"
        private const val PARAMETERS = "parameters"
    }

}