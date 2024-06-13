package william.miranda.downloader

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import william.miranda.downloader.exception.DownloadErrorException
import william.miranda.downloader.exception.UrlNotFoundException
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import javax.inject.Inject

/**
 * Download a file
 */
class Downloader @Inject constructor(
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun download(
        sourceUrl: String,
        targetFile: File
    ) {
        withContext(dispatcher) {

            try {
                URL(sourceUrl).openStream().use { inputStream ->
                    targetFile.outputStream().use { outputStream ->
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead: Int

                        //Read from the Input Stream respecting Buffer size and writes to the Output Stream
                        while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                    }
                }
            } catch (ex: FileNotFoundException) {
                throw UrlNotFoundException(ex.message ?: "")
            } catch (ex: Exception) {
                throw DownloadErrorException(ex.message)
            }
        }
    }
}