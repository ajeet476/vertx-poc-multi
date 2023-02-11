package io.ajeet.poc.api.controller

import io.ajeet.poc.api.service.TokenService
import io.ajeet.poc.common.mapper.JsonMapper
import io.vertx.ext.web.RoutingContext
import java.util.*

class TokenController(private val tokenService: TokenService) {
    fun generateToken(ctx: RoutingContext) {
        val user = UserDto(UUID.randomUUID(), "@todo")
        val tokenDto = this.tokenService.generateToken(user)

        ctx.end(JsonMapper.convert(tokenDto))
    }
}
