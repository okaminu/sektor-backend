package lt.tlistas.loginn.backend.test.unit

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import lt.tlistas.core.service.LocationLoggingService
import lt.tlistas.core.service.UserService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.core.type.entity.Company
import lt.tlistas.core.type.entity.User
import lt.tlistas.core.type.value_object.TimeRange
import lt.tlistas.loginn.backend.CollaboratorHandler
import lt.tlistas.loginn.backend.Routes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class CollaboratorHandlerTest {

    @Mock
    private lateinit var userServiceMock: UserService

    @Mock
    private lateinit var locationLoggingServiceMock: LocationLoggingService

    private lateinit var collaboratorHandler: CollaboratorHandler

    @Before
    fun setUp() {
        collaboratorHandler = CollaboratorHandler(userServiceMock, locationLoggingServiceMock)
    }

	@Test
	fun `Takes collaborator worktime`() {

		val timeRange = TimeRange(0, 1)
		val user = User().apply {
			company = Company().apply {
				addCollaborator(Collaborator()
						.apply { workTime = timeRange })
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

        assertEquals(timeRange, returnResult.responseBody)

	}
}
