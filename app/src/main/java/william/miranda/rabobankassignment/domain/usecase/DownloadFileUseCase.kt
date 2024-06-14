package william.miranda.rabobankassignment.domain.usecase

import william.miranda.downloader.Downloader
import java.io.File
import javax.inject.Inject

class DownloadFileUseCase @Inject constructor(
    private val downloader: Downloader
) {

    suspend fun run(
        sourceUrl: String,
        targetFile: File
    ) {
        downloader.download(
            sourceUrl = sourceUrl,
            targetFile = targetFile
        )
    }
}