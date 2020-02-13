package lt.boldadmin.sektor.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.api.event.publisher.CollaboratorCoordinatesPublisher
import lt.boldadmin.nexus.api.service.collaborator.CollaboratorService
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.api.type.valueobject.DayMinuteInterval
import lt.boldadmin.nexus.api.type.valueobject.MinuteInterval
import lt.boldadmin.sektor.backend.handler.identityconfirmed.CollaboratorHandler
import lt.boldadmin.sektor.backend.route.Routes
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.toMono
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.TUESDAY
import java.util.*

@ExtendWith(MockitoExtension::class)
class CollaboratorHandlerTest {

    @Mock
    private lateinit var collaboratorServiceStub: CollaboratorService

    @Mock
    private lateinit var identityConfirmationStub: IdentityConfirmation

    @Mock
    private lateinit var coordinatesPublisherSpy: CollaboratorCoordinatesPublisher

    private lateinit var webTestClient: WebTestClient


    @BeforeEach
    fun setUp() {
        val collaboratorAuthService = CollaboratorAuthenticationService(
            collaboratorServiceStub,
            identityConfirmationStub
        )

        val collaboratorHandler = CollaboratorHandler(collaboratorAuthService, coordinatesPublisherSpy)
        val routerFunction = Routes(mock(), collaboratorHandler, mock(), mock()).router()
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
        doReturn(USER_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)
    }

    @Test
    fun `Takes collaborator work time`() {
        val workWeek = sortedSetOf(
            DayMinuteInterval(TUESDAY, MinuteInterval(0, 1), false),
            DayMinuteInterval(MONDAY, MinuteInterval(0, 1), true)
        )
        doReturn(Collaborator().apply { this.workWeek = workWeek }).`when`(collaboratorServiceStub).getById(USER_ID)

        val workTimeResponseBody = webTestClient.get()
            .uri("/collaborator/work-week")
            .header("auth-token", AUTH_TOKEN)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(object: ParameterizedTypeReference<SortedSet<DayMinuteInterval>>() {})
            .returnResult()

        assertEquals(workWeek, workTimeResponseBody.responseBody)
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

        verify(coordinatesPublisherSpy).publish(USER_ID, coordinates)
    }

    companion object {
        private const val USER_ID = "userId"
        private const val AUTH_TOKEN = "asda454s6d"
    }
}
