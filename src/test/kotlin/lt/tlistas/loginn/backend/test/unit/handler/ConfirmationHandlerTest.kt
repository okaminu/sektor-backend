package lt.tlistas.loginn.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.crowbar.service.ConfirmationService
import lt.tlistas.loginn.backend.Routes
import lt.tlistas.loginn.backend.handler.ConfirmationHandler
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient

@RunWith(MockitoJUnitRunner::class)
class ConfirmationHandlerTest {

    @Mock
    private lateinit var confirmationCodeServiceMock: ConfirmationService

    @Mock
    private lateinit var collaboratorServiceMock: CollaboratorService

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Test
    fun `Sends confirmation code to existing collaborator`() {
        val collaborator = Collaborator().apply {
            id = "sadf5a4d5f64a"
            mobileNumber = "+37012345678"
        }
        val handler = ConfirmationHandler(collaboratorServiceMock, confirmationCodeServiceMock)
        doReturn(collaborator).`when`(collaboratorServiceMock).getByMobileNumber(collaborator.mobileNumber)

        val webTestClient = WebTestClient
                .bindToRouterFunction(Routes(mock(), handler, mock())
                        .router()).build()
        webTestClient.post().uri("mobile/register/mobileNumber/${collaborator.mobileNumber}")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody().isEmpty

        verify(collaboratorServiceMock).getByMobileNumber(collaborator.mobileNumber)
        verify(confirmationCodeServiceMock).sendConfirmation(collaborator.mobileNumber, collaborator.id!!)
    }
}