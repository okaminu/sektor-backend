package lt.tlistas.loginn.backend.test.unit

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.core.service.confirmation.ConfirmationCodeService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.loginn.backend.ConfirmationHandler
import lt.tlistas.loginn.backend.Routes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.web.reactive.server.WebTestClient

@RunWith(MockitoJUnitRunner::class)
class ConfirmationHandlerTest {

    @Mock
    private lateinit var collaboratorServiceMock: CollaboratorService

    @Mock
    private lateinit var confirmationCodeServiceMock: ConfirmationCodeService

    private lateinit var confirmationHandler: ConfirmationHandler

    @Before
    fun setUp() {
        confirmationHandler = ConfirmationHandler(collaboratorServiceMock, confirmationCodeServiceMock)
    }

    @Test
    fun `Sends confirmation code`() {
        val collaborator = Collaborator().apply {
            mobileNumber = "+37012345678"
        }
        doReturn(collaborator).`when`(collaboratorServiceMock).getByMobileNumber(any())

        val webTestClient = WebTestClient.bindToRouterFunction(Routes(mock(), confirmationHandler)
                .router()).build()
        webTestClient.post().uri("/collaborator/sendConfirmationCode/${collaborator.mobileNumber}")
                .exchange()

        verify(collaboratorServiceMock).getByMobileNumber(collaborator.mobileNumber)
        verify(confirmationCodeServiceMock).sendCodeToCollaborator(collaborator)
    }
}