package william.miranda.downloader.exception

class UrlNotFoundException(
    url: String
) : IllegalStateException("$URL_NOT_FOUND $url") {

    private companion object {
        private const val URL_NOT_FOUND = "Url not found:"
    }
}