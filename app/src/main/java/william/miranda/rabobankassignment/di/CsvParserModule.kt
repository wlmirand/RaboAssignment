package william.miranda.rabobankassignment.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.joda.time.LocalDateTime
import william.miranda.csvparser.CsvParser
import william.miranda.rabobankassignment.data.adapter.IsoDateTimeAdapter

/**
 * Hilt Module to Provide the instance of the CsvParser from our external Library
 */
@Module
@InstallIn(SingletonComponent::class)
object CsvParserModule {

    @Provides
    fun provideCsvParser(): CsvParser {
        return CsvParser().apply {
            registerTypeAdapter(
                clazz = LocalDateTime::class,
                adapter = IsoDateTimeAdapter()
            )
        }
    }

}