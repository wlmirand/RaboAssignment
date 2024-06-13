package william.miranda.csvparser.exceptions

class FileNotExistsException(
    absolutePath: String
) : IllegalStateException("$FILE_NOT_EXISTS $absolutePath") {

    private companion object {
        private const val FILE_NOT_EXISTS = "File not exists:"
    }

}