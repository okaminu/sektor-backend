package lt.boldadmin.sektor.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.type.Location
import lt.boldadmin.nexus.service.CollaboratorService
import lt.boldadmin.nexus.service.LocationWorkLogService
import lt.boldadmin.nexus.type.entity.Collaborator
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler
import lt.boldadmin.sektor.backend.route.WorkLogRoutes
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
    private lateinit var identityConfirmationMock: IdentityConfirmation

    private lateinit var workLogHandler: WorkLogHandler

    @Before
    fun setUp() {
        workLogHandler = WorkLogHandler(
                collaboratorServiceMock,
                locationWorkLogServiceMock,
                identityConfirmationMock
        )
    }

    @Test
    fun `Logs work by given location`() {
        val location = Location(1.1, 1.2)
        val collaborator = Collaborator()
        doReturn(USER_ID).`when`(identityConfirmationMock).getUserIdByToken(any())
        doReturn(collaborator).`when`(collaboratorServiceMock).getById(USER_ID)

        val webTestClient = WebTestClient
                .bindToRouterFunction(WorkLogRoutes(workLogHandler).router()).build()
        webTestClient.post().uri("/worklog/log-by-location")
                .header("auth-token",
                    AUTH_TOKEN
                )
                .body(location.toMono(), Location::class.java)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody().isEmpty

        verify(locationWorkLogServiceMock).logWork(collaborator, location)
    }

    companion object {
        private const val USER_ID = "userId"
        private const val AUTH_TOKEN = "asda454s6d"
    }
}
