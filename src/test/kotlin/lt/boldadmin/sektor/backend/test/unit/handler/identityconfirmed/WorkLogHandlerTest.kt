package lt.boldadmin.sektor.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.type.valueobject.Location
import lt.boldadmin.nexus.service.CollaboratorService
import lt.boldadmin.nexus.service.LocationWorkLogService
import lt.boldadmin.nexus.type.entity.Collaborator
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.service.WorkLogService
import lt.boldadmin.nexus.type.valueobject.TimeRange
import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler
import lt.boldadmin.sektor.backend.route.CollaboratorRoutes
import lt.boldadmin.sektor.backend.route.WorkLogRoutes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.toMono
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class WorkLogHandlerTest {

    @Mock
    private lateinit var locationWorkLogServiceMock: LocationWorkLogService

    @Mock
    private lateinit var collaboratorServiceMock: CollaboratorService

    @Mock
    private lateinit var identityConfirmationMock: IdentityConfirmation

    @Mock
    private lateinit var workLogServiceMock: WorkLogService

    private lateinit var workLogHandler: WorkLogHandler

    private lateinit var collaborator: Collaborator;

    @Before
    fun setUp() {
        workLogHandler = WorkLogHandler(
                collaboratorServiceMock,
                locationWorkLogServiceMock,
                identityConfirmationMock,
                workLogServiceMock
        )
        collaborator = Collaborator()
        doReturn(USER_ID).`when`(identityConfirmationMock).getUserIdByToken(AUTH_TOKEN)
        doReturn(collaborator).`when`(collaboratorServiceMock).getById(USER_ID)

    }

    @Test
    fun `Logs work by given location`() {
        val location = Location(1.1, 1.2)
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

    @Test
    fun `Project name of started work`() {
        val projectName = "ProjectName"
        doReturn(projectName).`when`(workLogServiceMock).getProjectNameOfStartedWork(USER_ID)

        val routerFunction = WorkLogRoutes(workLogHandler).router()
        val webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
        val returnResult = webTestClient.get()
            .uri("/worklog/project-name-of-started-work")
            .header("auth-token",
                    AUTH_TOKEN
            )
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(String::class.java)
            .returnResult()

        assertEquals(projectName, returnResult.responseBody)

    }

    companion object {
        private const val USER_ID = "userId"
        private const val AUTH_TOKEN = "asda454s6d"
    }
}
