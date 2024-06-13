package william.miranda.rabobankassignment.domain.usecase

import william.miranda.rabobankassignment.data.UserRepository
import william.miranda.rabobankassignment.domain.mapper.UserMapper
import william.miranda.rabobankassignment.domain.model.User
import java.io.File
import javax.inject.Inject

class ParseFileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    suspend fun run(
        sessionName: String,
        file: File,
        pageSize: Int
    ): List<User> {
        return userRepository.getUsers(
            sessionName = sessionName,
            file = file,
            pageSize = pageSize
        ).map { userMapper.map(it) }
    }
}