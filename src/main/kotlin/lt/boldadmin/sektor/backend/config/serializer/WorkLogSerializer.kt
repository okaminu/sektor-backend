package lt.boldadmin.sektor.backend.config.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import lt.boldadmin.nexus.type.entity.WorkLog

open class WorkLogSerializer : JsonSerializer<WorkLog>() {

    override fun serialize(workLog: WorkLog, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("id", workLog.id)
        gen.writeStringField("projectId", workLog.project.id)
        gen.writeStringField("collaboratorId", workLog.collaborator.id)
        gen.writeNumberField("timestamp", workLog.timestamp)
        gen.writeStringField("workStatus", workLog.workStatus.toString())
        gen.writeStringField("description", workLog.description)
        gen.writeStringField("intervalId", workLog.intervalId)
        gen.writeEndObject()
    }

}
