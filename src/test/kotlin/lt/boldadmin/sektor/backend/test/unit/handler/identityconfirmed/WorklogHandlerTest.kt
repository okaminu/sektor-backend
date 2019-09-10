package lt.boldadmin.sektor.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.api.service.CollaboratorService
import lt.boldadmin.nexus.api.service.worklog.status.WorklogStartEndService
import lt.boldadmin.nexus.api.service.worklog.status.location.WorklogLocationService
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler
import lt.boldadmin.sektor.backend.route.Routes
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.toMono

@ExtendWith(MockitoExtension::class)
class WorklogHandlerTest {

    @Mock
    private lateinit var workLogLocationServiceSpy: WorklogLocationService

    @Mock
    private lateinit var collaboratorServiceStub: CollaboratorService

    @Mock
    private lateinit var identityConfirmationStub: IdentityConfirmation

    @Mock
    private lateinit var workLogStartEndServiceStub: WorklogStartEndService

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
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
    }

    @Test
    fun `Logs work by given location`() {
        val coordinates = Coordinates(1.1, 1.2)
        doReturn(USER_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)
        doReturn(collaborator).`when`(collaboratorServiceStub).getById(USER_ID)

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
        doReturn(USER_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)

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
        doReturn(USER_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)

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
