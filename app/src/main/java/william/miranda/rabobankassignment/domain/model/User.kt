package william.miranda.rabobankassignment.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Model to hold Records this is the Model for the Business
 * This serves to put that we can separate the Models between the layers.
 * In this case, the Date here will be a String formatted for the Locale we are
 */
@Parcelize
data class User(
    val uuid: UUID = UUID.randomUUID(),
    val firstName: String,
    val surname: String,
    val issueCount: Int,
    val dateOfBirth: String,
    val avatar: String
): Parcelable