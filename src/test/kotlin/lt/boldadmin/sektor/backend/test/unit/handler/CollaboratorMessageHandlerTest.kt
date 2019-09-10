package lt.boldadmin.sektor.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.event.Publisher
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.sektor.backend.handler.CollaboratorMessageHandler
import lt.boldadmin.sektor.backend.route.Routes
import lt.boldadmin.sektor.backend.service.JsonToMapConverter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.http.HttpMethod
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Suppress("UnassignedFluxMonoInstance")
@RunWith(MockitoJUnitRunner::class)
class CollaboratorMessageHandlerTest {

    @Mock
    private lateinit var collaboratorLocationPublisherSpy: Publisher

    @Mock
    private lateinit var jsonToMapConverterStub: JsonToMapConverter

    @Mock
    private lateinit var webClientSpy: WebClient

    private lateinit var handlerWebClient: WebTestClient

    @Before
    fun `Set up`() {
        val collaboratorMessageHandler = CollaboratorMessageHandler(
            collaboratorLocationPublisherSpy,
            jsonToMapConverterStub,
            webClientSpy
        )

        handlerWebClient = WebTestClient
            .bindToRouterFunction(
                Routes(mock(), mock(), collaboratorMessageHandler, mock()).router()
            ).build()
    }

    @Test
    fun `Subscribes to notification topic`() {
        val url = "http://project-sign.af.mil"
        val jsonBody = "{'key': 'value'}"
        val bodyMap = mapOf("Type" to "SubscriptionConfirmation", "SubscribeURL" to url)
        doReturn(bodyMap).`when`(jsonToMapConverterStub).convert(jsonBody)

        val responseSpy: Mono<ClientResponse> = mock()
        val requestBodyUriSpecSpy: RequestBodyUriSpec = mock()
        val requestBodySpecSpy: RequestBodySpec = mock()

        doReturn(mock()).`when`(responseSpy).block()
        doReturn(responseSpy).`when`(requestBodySpecSpy).exchange()
        doReturn(requestBodySpecSpy).`when`(requestBodyUriSpecSpy).uri(url)
        doReturn(requestBodyUriSpecSpy).`when`(webClientSpy).method(HttpMethod.GET)

        postToHandlerWebClient(jsonBody)

        verify(webClientSpy).method(HttpMethod.GET)
        verify(requestBodyUriSpecSpy).uri(url)
        verify(requestBodySpecSpy).exchange()
        verify(responseSpy).block()
    }

    @Test
    fun `Updates collaborator location when notification arrives`() {
        val jsonBody = "{'key': 'value'}"
        val jsonMessage = "{'message': 'contents'}"
        val bodyMap = mapOf("Type" to "Notification", "Message" to jsonMessage)
        val messageMap = mapOf(
            "destinationNumber" to "123456",
            "originationNumber" to "000000",
            "messageBody" to "messageBody"
        )

        doReturn(bodyMap).`when`(jsonToMapConverterStub).convert(jsonBody)
        doReturn(messageMap).`when`(jsonToMapConverterStub).convert(jsonMessage)

        postToHandlerWebClient(jsonBody)

        verify(collaboratorLocationPublisherSpy).publish(convertMessage(messageMap))
    }

    private fun postToHandlerWebClient(jsonBody: String) {
        handlerWebClient.post()
            .uri("/collaborator/location/message")
            .body(jsonBody.toMono(), String::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody().isEmpty
    }

    private fun convertMessage(messageMap: Map<String, String>): Message {
        return Message(
            messageMap["destinationNumber"]!!,
            messageMap["originationNumber"]!!,
            messageMap["messageBody"]!!
        )
    }
}