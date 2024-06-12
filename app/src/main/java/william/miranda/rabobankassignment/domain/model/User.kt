package william.miranda.rabobankassignment.domain.model

/**
 * Model to hold Records this is the Model for the Business
 * This serves to put that we can separate the Models between the layers.
 * In this case, the Date here will be a String formatted for the Locale we are
 */
data class User(
    val firstName: String,
    val surname: String,
    val issueCount: Int,
    val dateOfBirth: String,
    val avatar: String
)