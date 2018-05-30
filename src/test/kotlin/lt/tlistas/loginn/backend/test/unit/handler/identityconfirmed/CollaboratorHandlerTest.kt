package lt.tlistas.loginn.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.core.type.valueobject.TimeRange
import lt.tlistas.crowbar.IdentityConfirmation
import lt.tlistas.loginn.backend.handler.identityconfirmed.CollaboratorHandler
import lt.tlistas.loginn.backend.route.CollaboratorRoutes
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
    private lateinit var identityConfirmationMock: IdentityConfirmation

    private lateinit var collaboratorHandler: CollaboratorHandler

    @Before
    fun setUp() {
        collaboratorHandler = CollaboratorHandler(
            collaboratorServiceMock,
            identityConfirmationMock
        )
    }

    @Test
    fun `Takes collaborator work time`() {
        val workTime = TimeRange(0, 1)
        doReturn(USER_ID).`when`(identityConfirmationMock).getUserIdByToken(any())
        doReturn(Collaborator().apply { this.workTime = workTime }).`when`(collaboratorServiceMock).getById(USER_ID)

        val routerFunction = CollaboratorRoutes(collaboratorHandler, mock()).router()
        val webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
        val returnResult = webTestClient.get()
                .uri("/collaborator/workTime")
                .header("auth-token",
                    AUTH_TOKEN
                )
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
