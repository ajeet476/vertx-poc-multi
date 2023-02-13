package io.ajeet.poc.common.repository.model

import java.time.Instant
import java.util.*

data class UserTokenRecord(val userid: UUID?) {
    var token: UUID? = null
    var tokenData: String? = null
    var createdDate: Instant? = null
}
