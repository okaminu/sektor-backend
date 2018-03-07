package lt.tlistas.loginn.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.crowbar.service.AuthenticationService
import lt.tlistas.loginn.backend.Routes
import lt.tlistas.loginn.backend.handler.AuthenticationHandler
import org.junit.Test
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals

class AuthenticationHandlerTest {

    @Test
    fun `Sends token to collaborator`() {
        val authServiceMock = mock<AuthenticationService>()
        val confirmationCode = "123456"
        val authenticationToken = "5s4f65asd4f"
        doReturn(authenticationToken).`when`(authServiceMock).getAuthenticationToken(any())

        val webTestClient = WebTestClient
                .bindToRouterFunction(Routes(mock(), mock(), mock(), AuthenticationHandler(authServiceMock))
                        .router()).build()
        val returnResult = webTestClient.post()
                .uri("mobile/confirm/code/$confirmationCode")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(String::class.java)
                .returnResult()

        verify(authServiceMock).getAuthenticationToken(confirmationCode)
        assertEquals(authenticationToken, returnResult.responseBody)
    }

}