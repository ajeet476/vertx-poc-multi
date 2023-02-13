package io.ajeet.poc.common.repository

import io.ajeet.poc.common.repository.model.UserTokenRecord
import io.vertx.core.Future
import java.util.*

interface UserTokenRepository {
    fun getToken(userId: UUID, refreshToken: UUID): Future<UserTokenRecord?>
    fun saveToken(tokenRecord: UserTokenRecord): Future<Boolean>
}