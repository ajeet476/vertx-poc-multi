package io.ajeet.poc.api.controller

import io.ajeet.poc.api.MainVerticle
import io.ajeet.poc.api.service.UserService
import io.ajeet.poc.common.mapper.JsonMapper
import io.vertx.ext.web.RoutingContext
import java.util.*

class UserController(private val userService: UserService) {

    constructor(app: MainVerticle): this (
        app.userService
    )

    suspend fun findUser(ctx: RoutingContext) {
        ctx.pathParam("id")?.let {
            UUID.fromString(it)
        }?.let { id ->
            this.userService.findUser(id)?.let {
                ctx.end(JsonMapper.convert(it))
            }
        }

        ctx.response().setStatusCode(404).end()
    }
}
