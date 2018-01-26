package lt.tlistas.loginn.backend.test.unit

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.service.LocationLoggingService
import lt.tlistas.core.service.UserService
import lt.tlistas.core.type.Location
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.core.type.entity.Company
import lt.tlistas.core.type.entity.User
import lt.tlistas.core.type.value_object.TimeRange
import lt.tlistas.loginn.backend.CollaboratorHandler
import lt.tlistas.loginn.backend.Routes
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.toMono
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class CollaboratorHandlerTest {

    @Mock
    private lateinit var userServiceMock: UserService

    @Mock
    private lateinit var locationLoggingServiceMock: LocationLoggingService

    private lateinit var collaboratorHandler: CollaboratorHandler

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Before
    fun setUp() {
        collaboratorHandler = CollaboratorHandler(userServiceMock, locationLoggingServiceMock)
    }

    @Test
    fun `Takes collaborator worktime`() {

        val workTime = TimeRange(0, 1)
        val user = User().apply {
            company = Company().apply {
                addCollaborator(Collaborator()
                        .apply { this.workTime = workTime })
            }
        }
        doReturn(user).`when`(userServiceMock).getByEmail(any())

        val routerFunction = Routes(collaboratorHandler).router()
        val webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
        val returnResult = webTestClient.get().uri("/collaborator/workTime")
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
        val user = User().apply {
            company = Company().apply {
                addCollaborator(collaborator)
            }
        }
        doReturn(user).`when`(userServiceMock).getByEmail(any())

        val webTestClient = WebTestClient.bindToRouterFunction(Routes(collaboratorHandler).router()).build()
        webTestClient.post().uri("/collaborator/logWorkByLocation")
                .body(location.toMono(), Location::class.java)
                .exchange()

        verify(locationLoggingServiceMock).logWorkByLocation(eq(collaborator), eq(location))

    }
}
