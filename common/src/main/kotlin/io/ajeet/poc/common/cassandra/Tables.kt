package io.ajeet.poc.common.cassandra

object UserTable {
    const val name: String = "users"
    enum class Fields {
        userid,
        firstname,
        lastname,
        email,
        created_date
    }
}

object UserLoginTable {
    const val name: String = "user_credentials"
    enum class Fields {
        userid,
        email,
        password
    }
}

object UserTokenTable {
    const val name: String = "user_login_tokens"
    enum class Fields {
        userid,
        token,
        created_date,
        token_data
    }
}
