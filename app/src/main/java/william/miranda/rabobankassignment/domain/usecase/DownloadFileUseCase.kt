package william.miranda.rabobankassignment.domain.usecase

import william.miranda.downloader.Downloader
import william.miranda.rabobankassignment.data.UserRepository
import william.miranda.rabobankassignment.domain.mapper.UserMapper
import william.miranda.rabobankassignment.domain.model.User
import java.io.File
import javax.inject.Inject

class DownloadFileUseCase @Inject constructor(
    private val downloader: Downloader
) {

    suspend fun run(
        sourceUrl: String,
        targetFile: File
    ) {
        return downloader.download(
            sourceUrl = sourceUrl,
            targetFile = targetFile
        )
    }
}