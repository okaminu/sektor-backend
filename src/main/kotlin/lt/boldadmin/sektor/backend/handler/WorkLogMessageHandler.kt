package lt.boldadmin.sektor.backend.handler

import lt.boldadmin.nexus.service.worklog.status.message.WorkLogMessageService
import lt.boldadmin.nexus.type.valueobject.Message
import lt.boldadmin.sektor.backend.service.JsonToMapConverter
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

open class WorkLogMessageHandler(
    private val workLogMessageService: WorkLogMessageService,
    private val jsonToMapConverter: JsonToMapConverter,
    private val webClient: WebClient
) {

    //todo this needs tests
    open fun logByMessage(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<String>()
            .doOnNext {
                val requestBodyMap = jsonToMapConverter.convert(it)

                if (requestBodyMap["Type"] == "SubscriptionConfirmation") {
                    webClient.method(HttpMethod.GET).uri(requestBodyMap["SubscribeURL"]!!).exchange().block()
                }

                if (requestBodyMap["Type"] == "Notification") {
                    workLogMessageService.logWork(createMessage(requestBodyMap["Message"]!!))
                }
            }.flatMap { ok().build() }

    private fun createMessage(json: String): Message {
        val map = jsonToMapConverter.convert(json)
        return Message(
            map["destinationNumber"]!!,
            map["originationNumber"]!!,
            map["messageBody"], ""
        )
    }
}