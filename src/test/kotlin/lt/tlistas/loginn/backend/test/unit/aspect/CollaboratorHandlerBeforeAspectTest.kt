package lt.tlistas.loginn.backend.test.unit.aspect

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.loginn.backend.aspect.CollaboratorHandlerBeforeAspect
import lt.tlistas.mobile.number.confirmation.api.exception.AuthenticationException
import lt.tlistas.mobile.number.confirmation.service.AuthenticationService
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.web.reactive.function.server.ServerRequest

@RunWith(MockitoJUnitRunner::class)
class CollaboratorHandlerBeforeAspectTest {

    @Mock
    private lateinit var authenticationServiceMock: AuthenticationService

    @Mock
    private lateinit var serverRequestMock: ServerRequest

    @Mock
    private lateinit var headersMock: ServerRequest.Headers

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    private lateinit var beforeAuthenticationAspect: CollaboratorHandlerBeforeAspect

    @Before
    fun `Set up`() {
        beforeAuthenticationAspect = CollaboratorHandlerBeforeAspect(authenticationServiceMock)
    }

    @Test
    fun `Checks if user is authenticated`() {
        mockHeaderResponse()
        doReturn(true).`when`(authenticationServiceMock).tokenExists(HEADER_LIST[0])

        beforeAuthenticationAspect.authenticate(serverRequestMock)

        verify(authenticationServiceMock).tokenExists(HEADER_LIST[0])
    }

    @Test
    fun `Throws exception when user is unauthenticated`() {
        expectedException.expect(AuthenticationException::class.java)
        mockHeaderResponse()
        doReturn(false).`when`(authenticationServiceMock).tokenExists(HEADER_LIST[0])

        beforeAuthenticationAspect.authenticate(serverRequestMock)
    }

    private fun mockHeaderResponse() {
        doReturn(headersMock).`when`(serverRequestMock).headers()
        doReturn(HEADER_LIST).`when`(headersMock).header("auth-token")
    }

    companion object {
        private val HEADER_LIST = listOf("saf654as6df48a")
    }
}