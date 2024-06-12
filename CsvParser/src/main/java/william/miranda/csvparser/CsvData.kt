package william.miranda.csvparser

/**
 * Class to hold the data for a CSV file
 * Comma Separated Values is a type of file that stores tabular data where commas separate
 * the values and newlines separates the records.
 *
 * For all CSV files parsed here, we expect the 1st line for Header containing the Column Names
 * and then each line containing a Record
 */
data class CsvData(
    val header: List<String>,
    val records: List<List<String>>
)