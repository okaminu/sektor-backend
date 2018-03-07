package lt.tlistas.loginn.backend.test.unit.aspect

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.crowbar.service.ConfirmationService
import lt.tlistas.loginn.backend.aspect.CollaboratorHandlerAspect
import lt.tlistas.loginn.backend.exception.IncorrectTokenException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.web.reactive.function.server.ServerRequest

@RunWith(MockitoJUnitRunner::class)
class CollaboratorHandlerAspectTest {

    @Mock
    private lateinit var headersMock: ServerRequest.Headers
    @Mock
    private lateinit var confirmationServiceMock: ConfirmationService

    @Mock
    private lateinit var serverRequestMock: ServerRequest

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    private lateinit var collaboratorHandlerAspect: CollaboratorHandlerAspect

    @Before
    fun `Set up`() {
        collaboratorHandlerAspect = CollaboratorHandlerAspect(confirmationServiceMock)
    }

    @Test
    fun `Checks if user is authenticated`() {
        mockHeaderResponse()
        doReturn(true).`when`(confirmationServiceMock).tokenExists(HEADER_LIST[0])

        collaboratorHandlerAspect.authenticate(serverRequestMock)

        verify(confirmationServiceMock).tokenExists(HEADER_LIST[0])
    }

    @Test
    fun `Throws exception when user is unauthenticated`() {
        expectedException.expect(IncorrectTokenException::class.java)
        mockHeaderResponse()
        doReturn(false).`when`(confirmationServiceMock).tokenExists(HEADER_LIST[0])

        collaboratorHandlerAspect.authenticate(serverRequestMock)
    }

    @Test
    fun `Throws exception when token is not provided`() {
        expectedException.expect(IncorrectTokenException::class.java)
        doReturn(headersMock).`when`(serverRequestMock).headers()
        doReturn(emptyList<String>()).`when`(headersMock).header("auth-token")

        collaboratorHandlerAspect.authenticate(serverRequestMock)
    }

    private fun mockHeaderResponse() {
        doReturn(headersMock).`when`(serverRequestMock).headers()
        doReturn(HEADER_LIST).`when`(headersMock).header("auth-token")
    }

    companion object {
        private val HEADER_LIST = listOf("saf654as6df48a")
    }
}