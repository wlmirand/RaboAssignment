package william.miranda.rabobankassignment.data.adapter

import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import william.miranda.csvparser.adapter.CsvTypeAdapter

/**
 * Adapter that tells our Csv Library how to convert a String into a LocalDateTime from Joda
 */
class IsoDateTimeAdapter : CsvTypeAdapter<LocalDateTime> {

    private companion object {
        private const val DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
    }

    override fun convert(value: String): LocalDateTime {
        val formatter = DateTimeFormat.forPattern(DATE_TIME_PATTERN)
        return LocalDateTime.parse(value, formatter)
    }
}