package lt.boldadmin.sektor.backend.test.unit.handler

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.api.service.collaborator.CollaboratorService
import lt.boldadmin.nexus.api.type.entity.collaborator.Collaborator
import lt.boldadmin.sektor.backend.handler.IdentityConfirmationHandler
import lt.boldadmin.sektor.backend.route.Routes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(MockitoExtension::class)
class IdentityConfirmationHandlerTest {

    @Mock
    private lateinit var identityConfirmationMock: IdentityConfirmation

    @Mock
    private lateinit var collaboratorServiceMock: CollaboratorService

    private lateinit var identityConfirmationHandler: IdentityConfirmationHandler

    @BeforeEach
    fun `Set up`() {
        identityConfirmationHandler = IdentityConfirmationHandler(identityConfirmationMock, collaboratorServiceMock)
    }

    @Test
    fun `Requests confirmation code`() {
        val collaborator = Collaborator().apply {
            id = "sadf5a4d5f64a"
            mobileNumber = "+37012345678"
        }
        doReturn(collaborator).`when`(collaboratorServiceMock).getByMobileNumber(collaborator.mobileNumber)

        val webTestClient = WebTestClient
            .bindToRouterFunction(
                Routes(mock(), mock(), mock(), identityConfirmationHandler).router()
            ).build()
        webTestClient.post()
            .uri("/collaborator/identity-confirmation/code/request/${collaborator.mobileNumber}")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody().isEmpty

        verify(collaboratorServiceMock).getByMobileNumber(collaborator.mobileNumber)
        verify(identityConfirmationMock).sendConfirmationCode(collaborator.id, collaborator.mobileNumber)
    }

    @Test
    fun `Confirms collaborator and returns token`() {
        val confirmationCode = "123456"
        val authenticationToken = "5s4f65asd4f"
        val userId = "userId"
        doReturn(userId).`when`(identityConfirmationMock).getUserIdByCode(confirmationCode)
        doReturn(authenticationToken).`when`(identityConfirmationMock).getTokenById(userId)

        val webTestClient = WebTestClient
            .bindToRouterFunction(
                Routes(mock(), mock(), mock(), identityConfirmationHandler).router()
            ).build()
        val returnResult = webTestClient.post()
            .uri("/collaborator/identity-confirmation/code/confirm/$confirmationCode")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(String::class.java)
            .returnResult()

        verify(identityConfirmationMock).getTokenById(userId)
        verify(identityConfirmationMock).confirmCode(confirmationCode)
        verify(identityConfirmationMock).getTokenById(userId)
        assertEquals(authenticationToken, returnResult.responseBody)
    }

}
