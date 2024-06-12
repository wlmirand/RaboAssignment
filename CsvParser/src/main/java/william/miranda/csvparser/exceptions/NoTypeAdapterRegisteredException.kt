package william.miranda.csvparser.exceptions

class NoTypeAdapterRegisteredException(type: String) :
    IllegalStateException("$NO_TYPE_ADAPTER_REGISTERED $type") {

    private companion object {
        private const val NO_TYPE_ADAPTER_REGISTERED = "No Type Adapter registered for:"
    }

}