package lt.boldadmin.sektor.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.api.service.CollaboratorService
import lt.boldadmin.nexus.api.service.worklog.WorklogDurationService
import lt.boldadmin.nexus.api.service.worklog.WorklogService
import lt.boldadmin.nexus.api.service.worklog.WorklogStartEndService
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorklogHandler
import lt.boldadmin.sektor.backend.route.Routes
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
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
    private lateinit var workLogStartEndServiceStub: WorklogStartEndService

    private lateinit var webTestClient: WebTestClient

    @Before
    fun setUp() {
        val collaboratorAuthService = CollaboratorAuthenticationService(
            collaboratorServiceStub,
            identityConfirmationStub
        )
        val workLogHandler = WorklogHandler(
            collaboratorAuthService,
            worklogServiceStub,
            workLogStartEndServiceStub,
            workLogDurationServiceStub
        )
        val routerFunction = Routes(workLogHandler, mock(), mock(), mock()).router()
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()


        doReturn(COLLABORATOR_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)
    }



    @Test
    @Suppress("UNCHECKED_CAST")
    fun `Provides worklog interval endpoints`() {
        val expectedIntervalId = "intervalId"
        val workLogStub: Worklog = mock()
        val expectedWorkDuration = 1000L
        doReturn(expectedIntervalId).`when`(workLogStub).intervalId
        doReturn(listOf(workLogStub)).`when`(worklogServiceStub).getIntervalEndpoints(expectedIntervalId)
        doReturn(expectedWorkDuration).`when`(workLogDurationServiceStub).measureDuration(expectedIntervalId)

        val intervalEndpointsResponse = webTestClient.get()
            .uri("/worklog/interval/$expectedIntervalId/endpoints")
            .header(
                "auth-token",
                AUTH_TOKEN
            )
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(Map::class.java)
            .returnResult()

        assertEquals(
            expectedIntervalId,
            (intervalEndpointsResponse.responseBody!!["workLogs"] as List<Map<String, String>>)[0]["intervalId"]
        )
        assertEquals(expectedWorkDuration, (intervalEndpointsResponse.responseBody!!["workDuration"] as Int).toLong())
    }

    @Test
    fun `Provides worklog interval ids by collaborator`() {
        val expectedIntervalId1 = "id1"
        val expectedIntervalId2 = "id2"
        val workLogStub1: Worklog = mock()
        val workLogStub2: Worklog = mock()
        doReturn(listOf(workLogStub1, workLogStub1, workLogStub2))
            .`when`(worklogServiceStub)
            .getByCollaboratorId(COLLABORATOR_ID)
        doReturn(expectedIntervalId1).`when`(workLogStub1).intervalId
        doReturn(expectedIntervalId2).`when`(workLogStub2).intervalId

        val intervalIdsResponse = webTestClient.get()
            .uri("/worklog/collaborator/interval-ids")
            .header(
                "auth-token",
                AUTH_TOKEN
            )
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(Collection::class.java)
            .returnResult()

        assertEquals(listOf(expectedIntervalId1, expectedIntervalId2), intervalIdsResponse.responseBody)
    }

    @Test
    fun `Provides project name of started work`() {
        val expectedProject = Project(name = "projectName")
        doReturn(expectedProject).`when`(workLogStartEndServiceStub).getProjectOfStartedWork(COLLABORATOR_ID)

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
        doReturn(true).`when`(workLogStartEndServiceStub).hasWorkStarted(COLLABORATOR_ID)

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
