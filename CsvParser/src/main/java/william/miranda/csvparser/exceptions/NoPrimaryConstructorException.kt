package william.miranda.csvparser.exceptions

class NoPrimaryConstructorException : IllegalStateException(NO_PRIMARY_CONSTRUCTOR) {

    private companion object {
        private const val NO_PRIMARY_CONSTRUCTOR = "No primary constructor defined"
    }

}