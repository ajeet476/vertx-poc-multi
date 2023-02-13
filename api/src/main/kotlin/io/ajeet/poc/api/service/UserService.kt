package io.ajeet.poc.api.service

import io.ajeet.poc.api.controller.UserDto
import io.ajeet.poc.common.repository.UserRepository
import io.vertx.kotlin.coroutines.await
import java.util.*

class UserService(private val userRepository: UserRepository) {

    suspend fun findUser(id: UUID) : UserDto? {
       return this.userRepository.getUser(id).await()?.let {
            UserDto(it.userid, it.lastname)
        }
    }
}
