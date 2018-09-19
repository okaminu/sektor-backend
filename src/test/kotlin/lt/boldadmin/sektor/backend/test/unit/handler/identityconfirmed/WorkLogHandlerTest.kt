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
    private lateinit var locationWorkLogServiceSpy: LocationWorkLogService

    @Mock
    private lateinit var collaboratorServiceStub: CollaboratorService

    @Mock
    private lateinit var identityConfirmationStub: IdentityConfirmation

    @Mock
    private lateinit var workLogServiceSpy: WorkLogService

    private lateinit var webTestClient: WebTestClient

    @Before
    fun setUp() {
        val collaboratorAuthService = CollaboratorAuthenticationService(collaboratorServiceStub, identityConfirmationStub)
        val workLogHandler = WorkLogHandler(
            locationWorkLogServiceSpy,
            collaboratorAuthService,
            workLogServiceSpy
        )
        val routerFunction = Routes(workLogHandler, mock(), mock()).router()
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()


        doReturn(USER_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)
        doReturn(collaborator).`when`(collaboratorServiceStub).getById(USER_ID)
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

        verify(locationWorkLogServiceSpy).logWork(collaborator, location)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `Provides worklog interval endpoints`() {
        val expectedIntervalId = "intervalId"
        val workLogStub: WorkLog = mock()
        val expectedWorkDuration = 1000L
        doReturn(expectedIntervalId).`when`(workLogStub).intervalId
        doReturn(listOf(workLogStub)).`when`(workLogServiceSpy).getIntervalEndpoints(expectedIntervalId)
        doReturn(expectedWorkDuration).`when`(workLogServiceSpy).measureDuration(expectedIntervalId)

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
            (intervalEndpointsResponse.responseBody!!["workLogs"] as List<WorkLogAsJson>)[0]["intervalId"]
        )
        assertEquals(expectedWorkDuration.toInt(), intervalEndpointsResponse.responseBody!!["workDuration"])
    }

    @Test
    fun `Provides worklog interval ids by collaborator`() {
        val expectedIntervalId1 = "id1"
        val expectedIntervalId2 = "id2"
        val workLogStub1: WorkLog = mock()
        val workLogStub2: WorkLog = mock()
        doReturn(listOf(workLogStub1, workLogStub1, workLogStub2)).`when`(workLogServiceSpy).getByCollaboratorId(USER_ID)
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
        val expectedProjectName = "ProjectName"
        doReturn(expectedProjectName).`when`(workLogServiceSpy).getProjectNameOfStartedWork(USER_ID)

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

        assertEquals(expectedProjectName, projectNameResponse.responseBody)
    }

    @Test
    fun `Provides worklog description`() {
        val intervalId = "intervalId"
        val expectedDescription = "Description"
        doReturn(expectedDescription).`when`(workLogServiceSpy).getDescription(intervalId)

        val descriptionResponse = webTestClient.get()
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

        assertEquals(expectedDescription, descriptionResponse.responseBody!!.toString())
    }

    @Test
    fun `Provides work durations sum`() {
        val intervalIds = listOf("id1", "id2")
        val intervalIdsInUri = "id1,id2"
        val expectedDurationsSum = 1000L
        doReturn(expectedDurationsSum).`when`(workLogServiceSpy).sumWorkDurations(intervalIds)

        val durationSumResponse = webTestClient.get()
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

        assertEquals(expectedDurationsSum, durationSumResponse.responseBody!!.toLong())
    }

    @Test
    fun `Provides work status`() {
        doReturn(true).`when`(workLogServiceSpy).hasWorkStarted(USER_ID)

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

    @Test
    fun `Updates worklog description`() {
        val updatedDescription = "Updated Description"
        val intervalId = "intervalId"

        webTestClient.post()
            .uri("/worklog/interval/$intervalId/update-description")
            .header(
                "auth-token",
                AUTH_TOKEN
            )
            .body(updatedDescription.toMono(), String::class.java)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody().isEmpty

        verify(workLogServiceSpy).updateDescription(intervalId, updatedDescription)
    }

    companion object {
        private const val USER_ID = "userId"
        private const val AUTH_TOKEN = "asda454s6d"
        private val collaborator = Collaborator()
    }
}

private typealias WorkLogAsJson = Map<String, Any>
