package io.ajeet.poc.common.mapper

import com.fasterxml.jackson.databind.json.JsonMapper

class JsonMapper {
    companion object {
        private val mapper = JsonMapper()

        fun convert(dto: Any) : String {
            return mapper.writeValueAsString(dto)
        }
    }
}
