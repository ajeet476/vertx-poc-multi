package io.ajeet.poc.api.controller

import io.ajeet.poc.api.service.UserService
import io.ajeet.poc.common.mapper.JsonMapper
import io.vertx.ext.web.RoutingContext
import java.util.UUID

class UserController(private val userService: UserService) {
    fun findUser(ctx: RoutingContext) {
        ctx.pathParam("id")?.let {
            UUID.fromString(it)
        }?.let {
            val user = this.userService.findUser(it)
            ctx.end(JsonMapper.convert(user))
        }

        ctx.response().setStatusCode(404).end()
    }
}
