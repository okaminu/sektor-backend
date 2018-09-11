package lt.boldadmin.sektor.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.api.type.valueobject.Location
import lt.boldadmin.nexus.service.CollaboratorService
import lt.boldadmin.nexus.service.location.LocationWorkLogService
import lt.boldadmin.nexus.service.worklog.WorkLogService
import lt.boldadmin.nexus.type.entity.Collaborator
import lt.boldadmin.nexus.type.entity.WorkLog
import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler
import lt.boldadmin.sektor.backend.route.router
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
    private lateinit var locationWorkLogServiceMock: LocationWorkLogService

    @Mock
    private lateinit var collaboratorServiceMock: CollaboratorService

    @Mock
    private lateinit var identityConfirmationMock: IdentityConfirmation

    @Mock
    private lateinit var workLogServiceMock: WorkLogService

    private lateinit var collaborator: Collaborator

    private lateinit var webTestClient: WebTestClient

    @Before
    fun setUp() {
        val collaboratorAuthService = CollaboratorAuthenticationService(collaboratorServiceMock, identityConfirmationMock)
        val workLogHandler = WorkLogHandler(
            locationWorkLogServiceMock,
            collaboratorAuthService,
            workLogServiceMock
        )
        val routerFunction = router(workLogHandler, mock(), mock())()
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
        collaborator = Collaborator()
        doReturn(USER_ID).`when`(identityConfirmationMock).getUserIdByToken(AUTH_TOKEN)
        doReturn(collaborator).`when`(collaboratorServiceMock).getById(USER_ID)
    }

    @Test
    fun `Logs work by given location`() {
        val location = Location(1.1, 1.2)

        webTestClient.post()
            .uri("/worklog/log-by-location")
            .header(
                "auth-token",
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
    fun `Provides worklog interval endpoints`() {
        val intervalId = "intervalId"
        val workLogDummy = mock<WorkLog>()
        val workDuration = 1000L
        doReturn(intervalId).`when`(workLogDummy).intervalId
        doReturn(listOf(workLogDummy)).`when`(workLogServiceMock).getIntervalEndpoints(intervalId)
        doReturn(workDuration).`when`(workLogServiceMock).measureDuration(intervalId)

        val returnResult = webTestClient.get()
            .uri("/worklog/interval/$intervalId/endpoints")
            .header(
                "auth-token",
                AUTH_TOKEN
            )
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(Map::class.java)
            .returnResult()

        assertEquals(intervalId, ((returnResult.responseBody!!["workLogs"] as List<*>)[0] as Map<*, *>)["intervalId"])
        assertEquals(workDuration.toInt(), returnResult.responseBody!!["workDuration"])
    }

    @Test
    fun `Provides distinct worklog interval ids by collaborator`() {
        val intervalId1 = "id1"
        val intervalId2 = "id2"
        val workLogDummy1 = mock<WorkLog>()
        val workLogDummy2 = mock<WorkLog>()
        doReturn(listOf(workLogDummy1, workLogDummy1, workLogDummy2)).`when`(workLogServiceMock).getByCollaboratorId(USER_ID)
        doReturn(intervalId1).`when`(workLogDummy1).intervalId
        doReturn(intervalId2).`when`(workLogDummy2).intervalId

        val returnResult = webTestClient.get()
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

        assertEquals(listOf(intervalId1, intervalId2), returnResult.responseBody)
    }

    @Test
    fun `Provides project name of started work`() {
        val projectName = "ProjectName"
        doReturn(projectName).`when`(workLogServiceMock).getProjectNameOfStartedWork(USER_ID)

        val returnResult = webTestClient.get()
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

        assertEquals(projectName, returnResult.responseBody)
    }

    @Test
    fun `Provides worklog description`() {
        val intervalId = "intervalId"
        val description = "Description"
        doReturn(description).`when`(workLogServiceMock).getDescription(intervalId)

        val returnResult = webTestClient.get()
            .uri("/worklog/interval/$intervalId/description")
            .header(
                "auth-token",
                AUTH_TOKEN
            )
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(String::class.java)
            .returnResult()

        assertEquals(description, returnResult.responseBody!!.toString())
    }

    @Test
    fun `Provides work durations sum`() {
        val intervalIds = listOf("id1", "id2")
        val intervalIdsInUri = "id1,id2"
        val durationsSum = 1000L
        doReturn(durationsSum).`when`(workLogServiceMock).sumWorkDurations(intervalIds)

        val returnResult = webTestClient.get()
            .uri("/worklog/interval/$intervalIdsInUri/durations-sum")
            .header(
                "auth-token",
                AUTH_TOKEN
            )
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(Long::class.java)
            .returnResult()

        assertEquals(durationsSum, returnResult.responseBody!!.toLong())
    }

    @Test
    fun `Provides work status`() {
        doReturn(true).`when`(workLogServiceMock).hasWorkStarted(USER_ID)

        val returnResult = webTestClient.get()
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

        assertTrue(returnResult.responseBody!!)

    }

    @Test
    fun `Updates worklog description`() {
        val updatedDescription = "Updated Description"
        val intervalId = "intervalId"

        webTestClient.post()
            .uri("/worklog/update-description/$intervalId")
            .header(
                "auth-token",
                AUTH_TOKEN
            )
            .body(updatedDescription.toMono(), String::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody().isEmpty

        verify(workLogServiceMock).updateDescription(intervalId, updatedDescription)
    }

    companion object {
        private const val USER_ID = "userId"
        private const val AUTH_TOKEN = "asda454s6d"
    }
}
