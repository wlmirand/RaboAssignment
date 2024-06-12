package william.miranda.rabobankassignment.domain.usecase

import william.miranda.rabobankassignment.data.UserRepository
import william.miranda.rabobankassignment.domain.mapper.UserMapper
import william.miranda.rabobankassignment.domain.model.User
import java.net.URL
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    suspend fun get(file: String): List<User> {
        val url = URL(file)
        return userRepository.getUsers(url)
            .map { userMapper.map(it) }
    }
}