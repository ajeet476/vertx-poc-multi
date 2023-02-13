package io.ajeet.poc.common.repository.model

import java.time.Instant
import java.util.*

data class UserRecord(val userid: UUID?) {
    var firstname: String? = null
    var lastname: String? = null
    var email: String? = null
    var createdDate: Instant? = null
}
