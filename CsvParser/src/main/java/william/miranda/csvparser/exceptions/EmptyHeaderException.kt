package william.miranda.csvparser.exceptions

class EmptyHeaderException : IllegalStateException(EMPTY_HEADER) {

    private companion object {
        private const val EMPTY_HEADER = "Empty Header"
    }

}