package william.miranda.rabobankassignment.domain.formatter

import org.joda.time.LocalDateTime
import javax.inject.Inject

class DateFormatter @Inject constructor() {

    private companion object {
        private const val DATE_TIME_PATTERN = "dd/MM/yyyy"
    }

    fun format(dateTime: LocalDateTime): String {
        return dateTime.toString(DATE_TIME_PATTERN)
    }
}