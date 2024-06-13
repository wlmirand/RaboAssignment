package william.miranda.rabobankassignment.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.joda.time.LocalDateTime
import william.miranda.csvparser.parser.CsvParser
import william.miranda.rabobankassignment.data.adapter.IsoDateTimeAdapter
import javax.inject.Singleton

/**
 * Hilt Module to Provide the instance of the CsvParser from our external Library
 */
@Module
@InstallIn(SingletonComponent::class)
object CsvParserModule {

    @Singleton
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