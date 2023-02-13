package io.ajeet.poc.common.repository

import io.ajeet.poc.common.repository.model.UserRecord
import io.vertx.core.Future
import java.util.*

interface UserRepository {
    fun getUser(id: UUID): Future<UserRecord>?
}