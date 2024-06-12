package william.miranda.csvparser.exceptions

class NoHeaderMatchException(header: String) :
    IllegalStateException("$NO_HEADER_MATCH $header") {

    private companion object {
        private const val NO_HEADER_MATCH = "No Header match for:"
    }

}