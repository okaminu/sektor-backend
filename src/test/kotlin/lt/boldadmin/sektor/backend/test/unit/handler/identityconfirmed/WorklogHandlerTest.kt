package lt.boldadmin.sektor.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.api.service.CollaboratorService
import lt.boldadmin.nexus.api.service.worklog.WorklogDurationService
import lt.boldadmin.nexus.api.service.worklog.WorklogService
import lt.boldadmin.nexus.api.service.worklog.WorklogStatusService
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorklogHandler
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

@ExtendWith(MockitoExtension::class)
class WorklogHandlerTest {

    @Mock
    private lateinit var collaboratorServiceStub: CollaboratorService

    @Mock
    private lateinit var identityConfirmationStub: IdentityConfirmation

    @Mock
    private lateinit var worklogServiceStub: WorklogService

    @Mock
    private lateinit var workLogDurationServiceStub: WorklogDurationService

    @Mock
    private lateinit var workLogStatusServiceStub: WorklogStatusService

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUp() {
        val collaboratorAuthService = CollaboratorAuthenticationService(
            collaboratorServiceStub,
            identityConfirmationStub
        )
        val workLogHandler = WorklogHandler(collaboratorAuthService, workLogStatusServiceStub)
        val routerFunction = Routes(workLogHandler, mock(), mock(), mock()).router()
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
    }

    @Test
    fun `Provides project name of started work`() {
        val expectedProject = Project(name = "projectName")
        doReturn(expectedProject).`when`(workLogStatusServiceStub).getProjectOfStartedWork(COLLABORATOR_ID)
        doReturn(COLLABORATOR_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)

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
        doReturn(true).`when`(workLogStatusServiceStub).hasWorkStarted(COLLABORATOR_ID)
        doReturn(COLLABORATOR_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)

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
        private const val COLLABORATOR_ID = "collabId"
        private const val AUTH_TOKEN = "as454s6d"
    }
}
