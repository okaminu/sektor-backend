package lt.tlistas.loginn.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.loginn.backend.Routes
import lt.tlistas.loginn.backend.handler.AuthenticationHandler
import lt.tlistas.mobile.number.confirmation.service.AuthenticationService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class AuthenticationHandlerTest {

    @Mock
    private lateinit var authServiceMock: AuthenticationService

    @Test
    fun `Sends token to collaborator`() {
        val confirmationCode = "123456"
        val token = "5s4f65asd4f"
        doReturn(token).`when`(authServiceMock).getAuthenticationToken(any())

        val webTestClient = WebTestClient
                .bindToRouterFunction(Routes(mock(), mock(), AuthenticationHandler(authServiceMock))
                        .router()).build()
        val returnResult = webTestClient.post()
                .uri("confirmation/authenticate/code/$confirmationCode")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(String::class.java)
                .returnResult()

        verify(authServiceMock).getAuthenticationToken(confirmationCode)
        assertEquals(token, returnResult.responseBody)
    }

}