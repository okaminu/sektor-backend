package lt.boldadmin.sektor.backend.test.unit.aspect

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.sektor.backend.aspect.IdentityConfirmationAspect
import lt.boldadmin.sektor.backend.exception.IncorrectConfirmationCodeException
import lt.boldadmin.sektor.backend.exception.IncorrectTokenException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.reactive.function.server.ServerRequest

@ExtendWith(MockitoExtension::class)
class IdentityConfirmationAspectTest {

    @Mock
    private lateinit var headersMock: ServerRequest.Headers
    @Mock
    private lateinit var identityConfirmationMock: IdentityConfirmation

    @Mock
    private lateinit var serverRequestMock: ServerRequest

    private lateinit var identityConfirmationAspect: IdentityConfirmationAspect

    @BeforeEach
    fun `Set up`() {
        identityConfirmationAspect = IdentityConfirmationAspect(
                identityConfirmationMock
        )
    }

    @Test
    fun `Checks if authentication token exists`() {
        mockHeaderResponse()
        doReturn(true).`when`(identityConfirmationMock).doesTokenExist(HEADER_LIST[0])

        identityConfirmationAspect.tokenExistsAdvice(serverRequestMock)

        verify(identityConfirmationMock).doesTokenExist(HEADER_LIST[0])
    }

    @Test
    fun `Throws exception when authentication token is not found`() {
        mockHeaderResponse()
        doReturn(false).`when`(identityConfirmationMock).doesTokenExist(HEADER_LIST[0])

        assertThrows(IncorrectTokenException::class.java) {
            identityConfirmationAspect.tokenExistsAdvice(serverRequestMock)
        }
    }

    @Test
    fun `Throws exception when authentication token is not provided`() {
        doReturn(headersMock).`when`(serverRequestMock).headers()
        doReturn(emptyList<String>()).`when`(headersMock).header("auth-token")

        assertThrows(IncorrectTokenException::class.java) {
            identityConfirmationAspect.tokenExistsAdvice(serverRequestMock)
        }
    }

    @Test
    fun `Checks if user by the given confirmation code exists`() {
        doReturn(true).`when`(identityConfirmationMock).doesUserByCodeExist("confirmationCode")
        doReturn("confirmationCode").`when`(serverRequestMock).pathVariable("code")

        identityConfirmationAspect.confirmationCodeUserExistsAdvice(serverRequestMock)

        verify(identityConfirmationMock).doesUserByCodeExist("confirmationCode")
        verify(serverRequestMock).pathVariable("code")
    }

    @Test
    fun `Throws exception when confirmation code is incorrect`() {
        doReturn(false).`when`(identityConfirmationMock).doesUserByCodeExist("confirmationCode")
        doReturn("confirmationCode").`when`(serverRequestMock).pathVariable("code")

        assertThrows(IncorrectConfirmationCodeException::class.java) {
            identityConfirmationAspect.confirmationCodeUserExistsAdvice(serverRequestMock)
        }
    }

    private fun mockHeaderResponse() {
        doReturn(headersMock).`when`(serverRequestMock).headers()
        doReturn(HEADER_LIST).`when`(headersMock).header("auth-token")
    }

    companion object {
        private val HEADER_LIST = listOf("saf654as6df48a")
    }
}