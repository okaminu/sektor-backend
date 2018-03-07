package lt.tlistas.loginn.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.core.type.value_object.TimeRange
import lt.tlistas.crowbar.service.AuthenticationService
import lt.tlistas.loginn.backend.Routes
import lt.tlistas.loginn.backend.handler.CollaboratorHandler
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
    private lateinit var collaboratorServiceMock: CollaboratorService

    @Mock
    private lateinit var authServiceMock: AuthenticationService

    private lateinit var collaboratorHandler: CollaboratorHandler

    @Before
    fun setUp() {
        collaboratorHandler = CollaboratorHandler(collaboratorServiceMock, authServiceMock)
    }

    @Test
    fun `Takes collaborator work time`() {
        val workTime = TimeRange(0, 1)
        doReturn(USER_ID).`when`(authServiceMock).getUserId(any())
        doReturn(Collaborator().apply { this.workTime = workTime }).`when`(collaboratorServiceMock).getById(USER_ID)

        val routerFunction = Routes(collaboratorHandler, mock(), mock(), mock()).router()
        val webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
        val returnResult = webTestClient.get().uri("/collaborator/workTime")
                .header("auth-token", AUTH_TOKEN)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(TimeRange::class.java)
                .returnResult()

        assertEquals(workTime, returnResult.responseBody)

    }

    companion object {
        private const val USER_ID = "userId"
        private const val AUTH_TOKEN = "asda454s6d"
    }
}
