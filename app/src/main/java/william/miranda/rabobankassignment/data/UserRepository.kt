package william.miranda.rabobankassignment.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import william.miranda.csvparser.parser.CsvParser
import william.miranda.csvparser.parser.ParsingSession
import william.miranda.rabobankassignment.data.model.UserModel
import java.io.File
import javax.inject.Inject

/**
 * Repository class
 * This will be responsible to ask the CsvParser library to go and retrieve the Data
 */
class UserRepository @Inject constructor(
    private val csvParser: CsvParser,
    private val dispatcher: CoroutineDispatcher
) {

    private companion object {
        private const val COMMA = ','
    }

    /**
     * Get the Users, in this case from the Csv file
     */
    suspend fun getUsers(
        sessionName: String,
        file: File,
        pageSize: Int
    ): List<UserModel> {
        return withContext(dispatcher) {
            val session = csvParser.getSession(sessionName)
                ?: csvParser.createSession(
                    sessionName = sessionName,
                    file = file,
                    separator = COMMA
                )

            if (session.status == ParsingSession.SessionStatus.ONGOING) {
                session.parseRecords(
                    pageSize = pageSize,
                    skipMalformedRecords = true,
                    clazz = UserModel::class
                )
            } else {
                emptyList()
            }
        }
    }
}