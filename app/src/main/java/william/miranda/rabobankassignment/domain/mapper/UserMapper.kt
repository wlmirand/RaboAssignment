package william.miranda.rabobankassignment.domain.mapper

import william.miranda.rabobankassignment.domain.formatter.DateFormatter
import william.miranda.rabobankassignment.data.model.UserModel
import william.miranda.rabobankassignment.domain.model.User
import javax.inject.Inject

/**
 * Class to Map Models across layers
 * This could be done by a simple lambda, but having a class makes testing easier
 */
class UserMapper @Inject constructor(
    private val dateFormatter: DateFormatter
) {

    fun map(model: UserModel): User {
        return User(
            firstName = model.firstName,
            surname = model.surname,
            issueCount = model.issueCount,
            dateOfBirth = dateFormatter.format(model.dateOfBirth),
            avatar = model.avatar
        )
    }
}