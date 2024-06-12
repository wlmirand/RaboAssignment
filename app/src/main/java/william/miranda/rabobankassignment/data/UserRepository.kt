package william.miranda.rabobankassignment.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import william.miranda.csvparser.CsvParser
import william.miranda.rabobankassignment.data.model.UserModel
import java.net.URL
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
        private const val CSV_SEPARATOR = ','
    }

    /**
     * Get the Users, in this case from the Csv file
     */
    suspend fun getUsers(url: URL): List<UserModel> {
        return withContext(dispatcher) {
            val urlStream = url.openStream()
            csvParser.downloadAndParse(
                inputStream = urlStream,
                separator = CSV_SEPARATOR,
                UserModel::class
            )
        }
    }
}