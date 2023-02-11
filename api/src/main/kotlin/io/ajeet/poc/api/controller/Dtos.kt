package io.ajeet.poc.api.controller

import java.util.UUID

data class UserDto (val id: UUID, val name: String)

data class Token(val token: String, val expiresInSeconds: Int)

data class TokenDto (val refreshToken: Token, val accessToken: Token)
