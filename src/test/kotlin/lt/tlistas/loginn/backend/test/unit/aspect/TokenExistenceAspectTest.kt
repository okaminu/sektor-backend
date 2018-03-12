package lt.tlistas.loginn.backend.test.unit.aspect

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.crowbar.service.TokenService
import lt.tlistas.loginn.backend.aspect.TokenExistenceAspect
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
class TokenExistenceAspectTest {

    @Mock
    private lateinit var headersMock: ServerRequest.Headers
    @Mock
    private lateinit var tokenServiceMock: TokenService

    @Mock
    private lateinit var serverRequestMock: ServerRequest

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    private lateinit var collaboratorHandlerAspect: TokenExistenceAspect

    @Before
    fun `Set up`() {
        collaboratorHandlerAspect = TokenExistenceAspect(tokenServiceMock)
    }

    @Test
    fun `Checks if authentication token exists`() {
        mockHeaderResponse()
        doReturn(true).`when`(tokenServiceMock).tokenExists(HEADER_LIST[0])

        collaboratorHandlerAspect.tokenExistsAdvise(serverRequestMock)

        verify(tokenServiceMock).tokenExists(HEADER_LIST[0])
    }

    @Test
    fun `Throws exception when authentication token is not found`() {
        expectedException.expect(IncorrectTokenException::class.java)
        mockHeaderResponse()
        doReturn(false).`when`(tokenServiceMock).tokenExists(HEADER_LIST[0])

        collaboratorHandlerAspect.tokenExistsAdvise(serverRequestMock)
    }

    @Test
    fun `Throws exception when authentication token is not provided`() {
        expectedException.expect(IncorrectTokenException::class.java)
        doReturn(headersMock).`when`(serverRequestMock).headers()
        doReturn(emptyList<String>()).`when`(headersMock).header("auth-token")

        collaboratorHandlerAspect.tokenExistsAdvise(serverRequestMock)
    }

    private fun mockHeaderResponse() {
        doReturn(headersMock).`when`(serverRequestMock).headers()
        doReturn(HEADER_LIST).`when`(headersMock).header("auth-token")
    }

    companion object {
        private val HEADER_LIST = listOf("saf654as6df48a")
    }
}