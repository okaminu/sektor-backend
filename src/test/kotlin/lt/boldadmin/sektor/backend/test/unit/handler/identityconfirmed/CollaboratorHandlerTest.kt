package lt.boldadmin.sektor.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.api.event.Publisher
import lt.boldadmin.nexus.api.service.CollaboratorService
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.api.type.valueobject.TimeRange
import lt.boldadmin.sektor.backend.handler.identityconfirmed.CollaboratorHandler
import lt.boldadmin.sektor.backend.route.Routes
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.toMono
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class CollaboratorHandlerTest {

    @Mock
    private lateinit var collaboratorServiceStub: CollaboratorService

    @Mock
    private lateinit var identityConfirmationStub: IdentityConfirmation

    @Mock
    private lateinit var collaboratorLocationPublisherSpy: Publisher

    private lateinit var webTestClient: WebTestClient


    @Before
    fun setUp() {
        val collaboratorAuthService = CollaboratorAuthenticationService(
                collaboratorServiceStub,
                identityConfirmationStub
        )

        val collaboratorHandler = CollaboratorHandler(collaboratorAuthService, collaboratorLocationPublisherSpy)
        val routerFunction = Routes(mock(), collaboratorHandler, mock(), mock()).router()
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
        doReturn(USER_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)
    }

    @Test
    fun `Takes collaborator work time`() {
        val workTime = TimeRange(0, 1)
        doReturn(Collaborator().apply { this.workTime = workTime }).`when`(collaboratorServiceStub).getById(USER_ID)

        val workTimeResponseBody = webTestClient.get()
                .uri("/collaborator/workTime")
                .header("auth-token", AUTH_TOKEN)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(TimeRange::class.java)
                .returnResult()

        assertEquals(workTime, workTimeResponseBody.responseBody)
    }

    @Test
    fun `Updates collaborator location`() {
        val coordinates = Coordinates(1.1, 1.2)

        webTestClient.post()
            .uri("/collaborator/location/coordinates")
            .header("auth-token", AUTH_TOKEN)
            .body(coordinates.toMono(), Coordinates::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody().isEmpty

        verify(collaboratorLocationPublisherSpy).publish(USER_ID, coordinates)
    }

    companion object {
        private const val USER_ID = "userId"
        private const val AUTH_TOKEN = "asda454s6d"
    }
}
