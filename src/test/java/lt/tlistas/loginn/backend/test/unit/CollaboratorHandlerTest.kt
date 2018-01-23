package lt.tlistas.loginn.backend.test.unit

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import lt.tlistas.core.service.LocationLoggingService
import lt.tlistas.core.service.UserService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.core.type.entity.Company
import lt.tlistas.core.type.entity.User
import lt.tlistas.core.type.value_object.TimeRange
import lt.tlistas.loginn.backend.CollaboratorHandler
import lt.tlistas.loginn.backend.Routes
import org.junit.Test
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals


class CollaboratorHandlerTest {
	
	@Test
	fun `Takes collaborator worktime`() {
		val userServiceMock : UserService = mock()
		val locationLoggingService : LocationLoggingService= mock()

		val timeRange = TimeRange(0, 1)
		val user = User().apply {
			company = Company().apply {
				addCollaborator(Collaborator()
						.apply { workTime = timeRange })
			}
		}
		doReturn(user).`when`(userServiceMock).getByEmail(any())

		val collaboratorHandler = CollaboratorHandler(userServiceMock, locationLoggingService)

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
