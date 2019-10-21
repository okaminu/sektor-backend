package lt.boldadmin.sektor.backend.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

object JsonToMapConverter {
    internal fun convert(json: String) =
        ObjectMapper().readValue<Map<String, String>>(json, object: TypeReference<Map<String, String>>() {})
}
