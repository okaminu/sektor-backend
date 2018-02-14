package lt.tlistas.loginn.backend.test.unit

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.service.confirmation.AuthenticationTokenService
import lt.tlistas.core.service.confirmation.ConfirmationCodeService
import lt.tlistas.loginn.backend.ConfirmationHandler
import lt.tlistas.loginn.backend.Routes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertFalse

@RunWith(MockitoJUnitRunner::class)
class ConfirmationHandlerTest {

    @Mock
    private lateinit var tokenServiceMock: AuthenticationTokenService

    @Mock
    private lateinit var confirmationCodeServiceMock: ConfirmationCodeService

    private lateinit var confirmationHandler: ConfirmationHandler

    @Before
    fun setUp() {
        confirmationHandler = ConfirmationHandler(confirmationCodeServiceMock, tokenServiceMock)
    }

    @Test
    fun `Sends confirmation code to collaborator`() {
        val mobileNumber = "+37012345678"

        val webTestClient = WebTestClient.bindToRouterFunction(Routes(mock(), confirmationHandler)
                .router()).build()
        webTestClient.post().uri("/confirmation/confirm/number/$mobileNumber")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody().isEmpty

        verify(confirmationCodeServiceMock).sendCodeToCollaborator(mobileNumber)
    }

    @Test
    fun `Sends confirmation token to collaborator`() {
        val confirmationCode = "123456"
        val token = "5s4f65asd4f"
        doReturn(token).`when`(tokenServiceMock).getAuthorizationToken(any())

        val webTestClient = WebTestClient.bindToRouterFunction(Routes(mock(), confirmationHandler)
                .router()).build()
        val returnResult = webTestClient.post()
                .uri("confirmation/authenticate/code/$confirmationCode")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody(String::class.java)
                .returnResult()

        verify(tokenServiceMock).getAuthorizationToken(confirmationCode)
        assertFalse(returnResult.responseBody.isEmpty())
    }
}