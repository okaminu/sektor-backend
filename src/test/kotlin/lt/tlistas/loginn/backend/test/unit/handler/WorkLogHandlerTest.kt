package lt.tlistas.loginn.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.*
import lt.tlistas.core.api.type.Location
import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.core.service.LocationWorkLogService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.crowbar.service.AuthenticationService
import lt.tlistas.loginn.backend.Routes
import lt.tlistas.loginn.backend.handler.WorkLogHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.toMono

@RunWith(MockitoJUnitRunner::class)
class WorkLogHandlerTest {

    @Mock
    private lateinit var locationWorkLogServiceMock: LocationWorkLogService

    @Mock
    private lateinit var collaboratorServiceMock: CollaboratorService

    @Mock
    private lateinit var authServiceMock: AuthenticationService

    private lateinit var workLogHandler: WorkLogHandler

    @Before
    fun setUp() {
        workLogHandler = WorkLogHandler(collaboratorServiceMock, locationWorkLogServiceMock, authServiceMock)
    }

    @Test
    fun `Logs work by given location`() {
        val location = Location(1.1, 1.2)
        val collaborator = Collaborator()
        doReturn(USER_ID).`when`(authServiceMock).getUserId(any())
        doReturn(collaborator).`when`(collaboratorServiceMock).getById(USER_ID)

        val webTestClient = WebTestClient
                .bindToRouterFunction(Routes(mock(), mock(), workLogHandler, mock()).router()).build()
        webTestClient.post().uri("/worklog/log-by-location")
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
