package lt.boldadmin.sektor.backend.config.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import lt.boldadmin.nexus.type.entity.WorkLog

open class WorkLogSerializer : JsonSerializer<WorkLog>() {

    override fun serialize(workLog: WorkLog, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("intervalId", workLog.intervalId)
        gen.writeEndObject()
    }

}
