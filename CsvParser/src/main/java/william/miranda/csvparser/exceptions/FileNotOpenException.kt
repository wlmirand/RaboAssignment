package william.miranda.csvparser.exceptions

class FileNotOpenException : IllegalStateException(FILE_NOT_OPEN) {

    private companion object {
        private const val FILE_NOT_OPEN = "File not open"
    }

}