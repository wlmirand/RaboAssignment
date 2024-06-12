package william.miranda.rabobankassignment.data.model

import org.joda.time.LocalDateTime
import william.miranda.csvparser.CsvField

/**
 * Model to hold Records from the Csv File
 */
data class UserModel(
    @CsvField("First name") val firstName: String,
    @CsvField("Sur name") val surname: String,
    @CsvField("Issue count") val issueCount: Int,
    @CsvField("Date of birth") val dateOfBirth: LocalDateTime,
    @CsvField("avatar") val avatar: String
)