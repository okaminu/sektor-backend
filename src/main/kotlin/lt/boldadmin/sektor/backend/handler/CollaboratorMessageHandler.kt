package lt.boldadmin.sektor.backend.handler

import lt.boldadmin.nexus.api.event.publisher.CollaboratorMessagePublisher
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.sektor.backend.service.JsonToMapConverter
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

open class CollaboratorMessageHandler(
    private val publisher: CollaboratorMessagePublisher,
    private val jsonToMapConverter: JsonToMapConverter,
    private val webClient: WebClient
) {

    open fun updateLocationByMessage(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<String>()
            .doOnNext {
                val requestBodyMap = jsonToMapConverter.convert(it)

                if (requestBodyMap["Type"] == "SubscriptionConfirmation")
                    webClient.method(HttpMethod.GET).uri(requestBodyMap["SubscribeURL"]!!).exchange().block()

                if (requestBodyMap["Type"] == "Notification")
                    publisher.publish(convertMessage(requestBodyMap["Message"]!!))

            }.flatMap { ok().build() }

    private fun convertMessage(json: String): Message {
        val map = jsonToMapConverter.convert(json)
        return Message(
            map["destinationNumber"]!!,
            map["originationNumber"]!!,
            map["messageBody"]!!
        )
    }
}
