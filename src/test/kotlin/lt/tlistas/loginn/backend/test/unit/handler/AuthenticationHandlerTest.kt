package lt.tlistas.loginn.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.crowbar.service.RequestService
import lt.tlistas.crowbar.service.TokenService
import lt.tlistas.loginn.backend.handler.AuthenticationHandler
import lt.tlistas.loginn.backend.route.CollaboratorRoutes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class AuthenticationHandlerTest {

    @Mock
    private lateinit var tokenServiceMock: TokenService

    @Mock
    private lateinit var collaboratorServiceMock: CollaboratorService

    @Mock
    private lateinit var requestServiceMock: RequestService

    private lateinit var authenticationHandler: AuthenticationHandler

    @Before
    fun `Set up`() {
        authenticationHandler = AuthenticationHandler(requestServiceMock, tokenServiceMock,
                collaboratorServiceMock)
    }

    @Test
    fun `Requests confirmation code`() {
        val collaborator = Collaborator().apply {
            id = "sadf5a4d5f64a"
            mobileNumber = "+37012345678"
        }
        doReturn(collaborator).`when`(collaboratorServiceMock).getByMobileNumber(collaborator.mobileNumber)

        val webTestClient = WebTestClient
                .bindToRouterFunction(CollaboratorRoutes(mock(), authenticationHandler)
                        .router()).build()
        webTestClient.post()
                .uri("collaborator/authentication/code/request/${collaborator.mobileNumber}")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody().isEmpty

        verify(collaboratorServiceMock).getByMobileNumber(collaborator.mobileNumber)
        verify(requestServiceMock).sendConfirmation(collaborator.id!!, collaborator.mobileNumber)
    }

    @Test
    fun `Confirms collaborator and returns token`() {
        val confirmationCode = "123456"
        val authenticationToken = "5s4f65asd4f"
        doReturn(authenticationToken).`when`(tokenServiceMock).confirmCode(any())

        val webTestClient = WebTestClient
                .bindToRouterFunction(CollaboratorRoutes(mock(), authenticationHandler)
                        .router()).build()
        val returnResult = webTestClient.post()
                .uri("collaborator/authentication/code/confirm/$confirmationCode")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(String::class.java)
                .returnResult()

        verify(tokenServiceMock).confirmCode(confirmationCode)
        assertEquals(authenticationToken, returnResult.responseBody)
    }

}