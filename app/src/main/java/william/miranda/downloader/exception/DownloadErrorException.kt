package william.miranda.downloader.exception

class DownloadErrorException(
    message: String?
) : IllegalStateException("$DOWNLOAD_ERROR ${message ?: UNKNOWN_MESSAGE}") {

    private companion object {
        private const val DOWNLOAD_ERROR = "Download Error:"
        private const val UNKNOWN_MESSAGE = "Unknown"
    }
}