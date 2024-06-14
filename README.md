# Rabobank Assignment

As the requirements said, a **Csv Parser** should be implemented following the Android best practices along with code standards and Unit/UI Tests coverage.

The implementation of this project consists in an Android Application that contains a simple version of the App Architecture.
The layers are:
- UI
  - Implements the Activity and uses Jetpack Compose to render the contents
  - Implements the ViewModel which will invoke the Domain classes and exposes the Data
  - Holds utility Composable functions
- Domain
  - There are UseCases for the functions the App must perform like Download the file and Parse the file
  - Defines a Mapper to convert Models between the layers
  - Defines Formatters to convert data in a more convenient way
- Data
  - Layer responsible to fetch the data, in this case from a CSV file
  - Defines the Repository (which interacts with the **Csv Parser** module
  - Defines helper classes needed by the **Csv Parser**

## Csv Parser Module
From the UI perspective, we know all data will be displayed inside a LazyColumn (Compose) or a RecyclerView so, the user will see a list of a few items and will need to scroll down to see more so, if the Csv file is Huge, we could parse it at once and return a List containing all elements, but this would use lots of resources. To prevent this, the Parser will return a small number of Records (specified as a parameter) each time it is invoked and, the responsibility to manage all the "pages" should be done by the user of the Module.

The 2nd idea is that at this module level, we dont know anything about the Csv structure. We dont know how many columns and what are the data types for each column, so the Parser needs to receive a data class that acts like a Model class for the records presents in the Csv file and in short, was desired to have something like:
```
//Model for the Record
data class MyRecord(
    @SomeAnnotation("First Name") val firstName: String,
    @SomeAnnotation("Sur Name") val surname: String,
    @SomeAnnotation("Birth Date") val date: Date,
)

//Signature about how the Module should return the Data
val records: List<MyRecord> = parser.parse(...)
```

## Downloader
Since we needed to keep the File/Buffer open because we are parsing Records in small pages, I decided to write a small class responsible to Download the Csv file inside the App Cache folder and makes the **Csv Parser** to accept a File

