package william.miranda.csvparser.parser

import william.miranda.csvparser.adapters.CsvTypeAdapter
import william.miranda.csvparser.adapters.DoubleTypeAdapter
import william.miranda.csvparser.adapters.IntTypeAdapter
import william.miranda.csvparser.adapters.StringTypeAdapter
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

/**
 * Class that holds the logic to parse the a CSV File
 * The main goals are:
 * - We want a List<SomeModel> as a result
 * - We should parse the file in blocks so, long files will not break the code
 *
 * The general idea is to have an open "Session" where we will keep a Reader open while we have data to parse
 * When someone invokes this parser, we will return a PAGE containing PAGE_SIZE records.
 *
 * On next calls, we will return the next items so, the called needs to keep track of the state
 */
class CsvParser {

    /**
     * Container for all Type adapters that our Parser knows
     * We use this to use an instance of CsvTypeAdapter for a given type
     */
    private val adapters = mutableMapOf<KType, CsvTypeAdapter<Any>>()

    private val sessions = mutableMapOf<String, ParsingSession>()

    /**
     * Just add some default basic adapters
     * Note these may be replaced
     */
    init {
        registerTypeAdapter(String::class, StringTypeAdapter())
        registerTypeAdapter(Int::class, IntTypeAdapter())
        registerTypeAdapter(Double::class, DoubleTypeAdapter())
    }

    /**
     * Add a TypeAdapter to the Parser
     * Would be nice to having a Generic method, but the method then would have to be
     * inline fun <reified T>, which would not be able to access our private adapters val.
     * So either we keep this not so nice method signature, or we expose the adapters, which is not good too
     */
    fun registerTypeAdapter(clazz: KClass<*>, adapter: CsvTypeAdapter<Any>) {
        adapters[clazz.starProjectedType] = adapter
    }

    /**
     * Create a session to parse the File
     */
    fun createSession(
        sessionName: String,
        file: File,
        separator: Char
    ) = ParsingSession(
        file = file,
        separator = separator,
        adapters = adapters
    ).also { sessions[sessionName] = it }

    fun getSession(
        sessionName: String
    ) = sessions[sessionName]
}