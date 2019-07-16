package lt.boldadmin.sektor.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.api.service.CollaboratorService
import lt.boldadmin.nexus.api.service.worklog.WorklogService
import lt.boldadmin.nexus.api.service.worklog.duration.WorklogDurationService
import lt.boldadmin.nexus.api.service.worklog.status.WorklogStartEndService
import lt.boldadmin.nexus.api.service.worklog.status.location.WorklogLocationService
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler
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
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class WorkLogHandlerTest {

    @Mock
    private lateinit var workLogLocationServiceSpy: WorklogLocationService

    @Mock
    private lateinit var collaboratorServiceStub: CollaboratorService

    @Mock
    private lateinit var identityConfirmationStub: IdentityConfirmation

    @Mock
    private lateinit var worklogServiceStub: WorklogService

    @Mock
    private lateinit var workLogDurationServiceStub: WorklogDurationService

    @Mock
    private lateinit var workLogStartEndServiceStub: WorklogStartEndService

    private lateinit var webTestClient: WebTestClient

    @Before
    fun setUp() {
        val collaboratorAuthService = CollaboratorAuthenticationService(
            collaboratorServiceStub,
            identityConfirmationStub
        )
        val workLogHandler = WorkLogHandler(
            workLogLocationServiceSpy,
            collaboratorAuthService,
            workLogStartEndServiceStub
        )
        val routerFunction = Routes(workLogHandler, mock(), mock(), mock()).router()
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()


        doReturn(USER_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)
        doReturn(collaborator).`when`(collaboratorServiceStub).getById(USER_ID)
    }

    @Test
    fun `Logs work by given location`() {
        val coordinates = Coordinates(1.1, 1.2)

        webTestClient.post()
            .uri("/worklog/log-by-location")
            .header(
                "auth-token",
                AUTH_TOKEN
            )
            .body(coordinates.toMono(), Coordinates::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody().isEmpty

        verify(workLogLocationServiceSpy).logWork(collaborator, coordinates)
    }

    @Test
    fun `Provides project name of started work`() {
        val expectedProject = Project(name = "projectName")
        doReturn(expectedProject).`when`(workLogStartEndServiceStub).getProjectOfStartedWork(USER_ID)

        val projectNameResponse = webTestClient.get()
            .uri("/worklog/project-name-of-started-work")
            .header(
                "auth-token",
                AUTH_TOKEN
            )
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(String::class.java)
            .returnResult()

        assertEquals(expectedProject.name, projectNameResponse.responseBody)
    }

    @Test
    fun `Provides work status`() {
        doReturn(true).`when`(workLogStartEndServiceStub).hasWorkStarted(USER_ID)

        val hasWorkStartedResponse = webTestClient.get()
            .uri("/worklog/has-work-started")
            .header(
                "auth-token",
                AUTH_TOKEN
            )
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(Boolean::class.java)
            .returnResult()

        assertTrue(hasWorkStartedResponse.responseBody!!)
    }

    companion object {
        private const val USER_ID = "userId"
        private const val AUTH_TOKEN = "as454s6d"
        private val collaborator = Collaborator()
    }
}

private typealias WorkLogAsJson = Map<String, String>
