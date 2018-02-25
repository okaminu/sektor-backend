package lt.tlistas.loginn.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.*
import lt.tlistas.core.api.type.Location
import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.core.service.LocationWorkLogService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.core.type.value_object.TimeRange
import lt.tlistas.loginn.backend.Routes
import lt.tlistas.loginn.backend.handler.CollaboratorHandler
import lt.tlistas.mobile.number.confirmation.service.AuthenticationService
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
    private lateinit var locationWorkLogServiceMock: LocationWorkLogService

    @Mock
    private lateinit var collaboratorServiceMock: CollaboratorService

    @Mock
    private lateinit var authServiceMock: AuthenticationService

    private lateinit var collaboratorHandler: CollaboratorHandler

    @Before
    fun setUp() {
        collaboratorHandler = CollaboratorHandler(collaboratorServiceMock, locationWorkLogServiceMock, authServiceMock)
    }

    @Test
    fun `Takes collaborator work time`() {
        val workTime = TimeRange(0, 1)
        doReturn(USER_ID)
                .`when`(authServiceMock).getUserId(any())
        doReturn(Collaborator().apply { this.workTime = workTime })
                .`when`(collaboratorServiceMock).getById(USER_ID)

        val routerFunction = Routes(collaboratorHandler, mock(), mock()).router()
        val webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
        val returnResult = webTestClient.get().uri("/collaborator/workTime")
                .header("auth-token", AUTH_TOKEN)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(TimeRange::class.java)
                .returnResult()

        assertEquals(workTime, returnResult.responseBody)

    }

    @Test
    fun `Logs work by given location`() {
        val location = Location(1.1, 1.2)
        val collaborator = Collaborator()
        doReturn(USER_ID)
                .`when`(authServiceMock).getUserId(any())
        doReturn(collaborator)
                .`when`(collaboratorServiceMock).getById(USER_ID)

        val webTestClient = WebTestClient
                .bindToRouterFunction(Routes(collaboratorHandler, mock(), mock()).router()).build()
        webTestClient.post().uri("/collaborator/logWorkByLocation")
                .header("auth-token", AUTH_TOKEN)
                .body(location.toMono(), Location::class.java)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody().isEmpty

        verify(locationWorkLogServiceMock).logWork(eq(collaborator), eq(location))
    }

    companion object {
        private const val USER_ID = "userId"
        private const val AUTH_TOKEN = "asda454s6d"
    }
}
