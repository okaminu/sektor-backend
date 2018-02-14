package lt.tlistas.loginn.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.service.confirmation.ConfirmationService
import lt.tlistas.loginn.backend.Routes
import lt.tlistas.loginn.backend.handler.ConfirmationHandler
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient

@RunWith(MockitoJUnitRunner::class)
class ConfirmationHandlerTest {

    @Mock
    private lateinit var confirmationCodeServiceMock: ConfirmationService

    @Test
    fun `Sends confirmation code to collaborator`() {
        val mobileNumber = "+37012345678"

        val webTestClient = WebTestClient
                .bindToRouterFunction(Routes(mock(), ConfirmationHandler(confirmationCodeServiceMock), mock())
                        .router()).build()
        webTestClient.post().uri("/confirmation/confirm/number/$mobileNumber")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody().isEmpty

        verify(confirmationCodeServiceMock).sendConfirmation(mobileNumber)
    }
}