package lt.boldadmin.sektor.backend.config.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import lt.boldadmin.nexus.api.type.entity.Worklog

open class WorkLogSerializer : JsonSerializer<Worklog>() {

    override fun serialize(workLog: Worklog, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("intervalId", workLog.intervalId)
        gen.writeEndObject()
    }

}
