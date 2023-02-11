package io.ajeet.poc.api.service

import io.ajeet.poc.api.controller.UserDto
import java.util.UUID

class UserService {
    fun findUser(id: UUID) : UserDto {
        return UserDto(id, "@todo")
    }
}
